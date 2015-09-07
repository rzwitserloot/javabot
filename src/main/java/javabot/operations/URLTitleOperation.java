package javabot.operations;

import javabot.Message;
import javabot.operations.urlcontent.URLContentAnalyzer;
import javabot.operations.urlcontent.URLFromMessageParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class URLTitleOperation extends BotOperation {
    @Inject
    URLContentAnalyzer analyzer;
    @Inject
    URLFromMessageParser parser;

    static final RequestConfig requestConfig = RequestConfig.custom()
                                                            .setConnectionRequestTimeout(5000)
                                                            .setConnectTimeout(5000)
                                                            .setSocketTimeout(5000)
                                                            .build();

    @Override
    public boolean handleChannelMessage(final Message event) {
        final String message = event.getValue();
        try {
            List<String> titlesToPost = parser.urlsFromMessage(message).stream()
                    .map(URL::toString)
                    .map(s -> findTitle(s, true))
                    .filter((s -> s != null))
                    .collect(Collectors.toList());
            if (titlesToPost.isEmpty()) {
                return false;
            } else {
                postMessageToChannel(titlesToPost, event);
                return true;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return false;
        }
    }

    private void postMessageToChannel(List<String> titlesToPost, Message event) {
        String botMessage;
        if (titlesToPost.size() == 1) {
            botMessage = format("%s'%s title: %s",
                    event.getUser().getNick(),
                    event.getUser().getNick().endsWith("s") ? "" : "s",
                    titlesToPost.get(0));
        } else {
            botMessage = format("%s'%s titles: %s",
                    event.getUser().getNick(),
                    event.getUser().getNick().endsWith("s") ? "" : "s",
                    String.join(" | ", titlesToPost.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList())));
        }

        getBot().postMessageToChannel(event, botMessage);
    }

    private String findTitle(String url, boolean loop) {
        if (analyzer.precheck(url)) {
            try (CloseableHttpClient client = HttpClientBuilder
                                                  .create()
                                                  .setDefaultRequestConfig(requestConfig)
                                                  .build()) {
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0");
                HttpResponse response = client.execute(httpget);
                HttpEntity entity = response.getEntity();

                try {
                    if (!(response.getStatusLine().getStatusCode() == 404 ||
                          response.getStatusLine().getStatusCode() == 403) && entity != null) {

                        Document doc = Jsoup.parse(EntityUtils.toString(entity));
                        String title = clean(doc.title());
                        return (analyzer.check(url, title)) ? title : null;
                    } else {
                        return null;
                    }
                } finally {
                    EntityUtils.consume(entity);
                }
            } catch (IOException ioe) {
                if (loop && !url.substring(0, 10).contains("//www.")) {
                    String tUrl = url.replace("//", "//www.");
                    return findTitle(tUrl, false);
                } else {
                    return null;
                }
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        } else {
            return null;
        }
    }

    private String clean(String title) {
        StringBuilder sb = new StringBuilder();
        title.chars().filter(i -> i < 127).forEach(i -> sb.append((char) i));
        return sb.toString();
    }
}