package javabot.operations

import javabot.BaseTest
import javabot.Message
import javabot.operations.urlcontent.URLContentAnalyzer
import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import javax.inject.Inject

@Test(groups = arrayOf("operations"))
public class URLTitleOperationTest : BaseTest() {
    @Inject
    lateinit private var operation: URLTitleOperation
    private val analyzer = URLContentAnalyzer()

    @Test(dataProvider = "urls")
    public fun testSimpleUrl(url: String, content: String?) {
        val results = operation.handleChannelMessage(Message(testChannel, testUser, url))
        if (content != null) {
            Assert.assertEquals(results[0].value, content)
        } else {
            Assert.assertTrue(results.isEmpty(), "Results for '${url}' should be empty: ${results}")
        }
    }

    @Test(dataProvider = "urlRulesCheck")
    public fun testFuzzyContent(url: String, title: String?, pass: Boolean) {
        assertEquals(analyzer.check(url, title), pass)
    }

    @DataProvider(name = "urls")
    fun getUrls(): Array<Array<*>>  {
        return arrayOf(
                arrayOf("http://google.com/", null),
                arrayOf("http://google.com", null),
                arrayOf("Have you tried to http://google.com", null),
                arrayOf("http://varietyofsound.wordpress.com has a lot of VSTs", null),
                arrayOf("Have you tried to http://javachannel.org/", "title for the url from botuser: \"Freenode ##java  enthusiasts united\""),
                arrayOf("http://javachannel.org/posts/finding-hash-collisions-in-java-strings/", null),
                arrayOf("http://hastebin.com/askhjahs", null),
                arrayOf("http://pastebin.com/askhjahs", null),
                arrayOf("https://www.facebook.com/thechurchofspace", null), // url matches content title too well
                arrayOf("http://architects.dzone.com/articles/why-programmers-should-have",
                        "title for the url from botuser: \"Why Programmers Should Have a Blog - DZone Agile\""), // url matches title
                arrayOf("http://facebook.com/foo/bar/blah", null), // doesn't exist on facebook, I hope
                arrayOf("http://", null),
                arrayOf("http://docs.oracle.com/javaee/6/tutorial/doc/", "title for the url from botuser: \"- The Java EE 6 Tutorial\""),
                arrayOf("https://docs.oracle.com/javaee/7/api/javax/enterprise/inject/Instance.html", null),
                arrayOf("http://docs.oracle.com/javase/tutorial/java/nutsandbolts/branch.html",
                        "title for the url from botuser: \"Branching Statements (The Java Tutorials > Learning the Java Language > Language Basics)\""),
                arrayOf("http://git.io/foo", null),
                arrayOf("Two urls with titles: http://docs.oracle.com/javaee/6/tutorial/doc/ and http://javachannel.org/",
                        "titles for the urls from botuser: \"- The Java EE 6 Tutorial\" | \"Freenode ##java  enthusiasts united\""),
                arrayOf("Two urls, one with a title: http://javachannel.org/posts/finding-hash-collisions-in-java-strings/  and " +
                        "http://javachannel.org/", "title for the url from botuser: \"Freenode ##java  enthusiasts united\""))
    }

    @DataProvider(name = "urlRulesCheck")
    fun getUrlsForRulesCheck(): Array<Array<*>> {
        return arrayOf(arrayOf("http://pastebin.com", "pastebin for your wastebin", false),
              arrayOf("http://makemoneyfast.com/super-profit", "make money fast! super profit", false),
              arrayOf("http://varietyofsound.wordpress.com", "Variety Of Sound", false),
              arrayOf("http://javachannel.com", "Freenode ##java: for enthusiasts by enthusiasts", true),
              arrayOf("http://javachannel.com/exceptions", "Freenode ##java: How to properly handle exceptions", true),
              arrayOf("http://foo.bar.com", "", false),
              arrayOf("http://foo.bar.com", null, false))
    }

}