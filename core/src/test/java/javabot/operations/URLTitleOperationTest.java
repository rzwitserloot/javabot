package javabot.operations;

import javabot.BaseMessagingTest;
import javabot.operations.urlcontent.URLContentAnalyzer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = {"operations"})
public class URLTitleOperationTest extends BaseMessagingTest {
    URLContentAnalyzer analyzer = new URLContentAnalyzer();

    @DataProvider(name = "urls")
    Object[][] getUrls() {
        return new Object[][]{
                {"http://google.com/", null},
                {"http://google.com", null},
                {"Have you tried to http://google.com", null},
                {"http://varietyofsound.wordpress.com has a lot of VSTs", null},
                {"Have you tried to http://javachannel.org/", "botuser's title: Freenode ##java | enthusiasts united"},
                {"http://javachannel.org/posts/finding-hash-collisions-in-java-strings/", null},
                {"http://hastebin.com/askhjahs", null},
                {"http://pastebin.com/askhjahs", null},
        };
    }

    @Test(dataProvider = "urls")
    public void testSimpleUrl(String url, String content) {
        if(content!=null) {
            testMessage(url, content);
        } else {
            testMessage(url);
        }
    }

    @DataProvider(name = "urlRulesCheck")
    Object[][] getUrlsForRulesCheck() {
        return new Object[][]{
                {"http://pastebin.com", "pastebin for your wastebin", false},
                {"http://makemoneyfast.com/super-profit", "make money fast! super profit", false},
                {"http://varietyofsound.wordpress.com", "Variety Of Sound", false},
                {"http://javachannel.com", "Freenode ##java: for enthusiasts by enthusiasts", true},
                {"http://javachannel.com/exceptions", "Freenode ##java: How to properly handle exceptions", true},
                {"http://foo.bar.com", "", false},
                {"http://foo.bar.com", null, false},
        };
    }

    @Test(dataProvider = "urlRulesCheck")
    public void testFuzzyContent(String url, String title, boolean pass) {
        assertEquals(analyzer.check(url, title), pass);
    }
}
