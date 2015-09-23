package javabot.operations

import com.antwerkz.sofia.Sofia
import javabot.Message
import javabot.operations.locator.JCPJSRLocator
import org.pircbotx.Channel

import javax.inject.Inject

public class JSROperation : BotOperation() {
    Inject
    var locator: JCPJSRLocator

    override fun handleMessage(event: Message): Boolean {
        val message = event.value.toLowerCase()
        val channel = event.channel
        if ("jsr" == message) {
            bot.postMessageToChannel(event, Sofia.jsrMissing())
            return true
        } else {
            if (message.startsWith("jsr ")) {
                val jsrString = message.substring("jsr ".length())

                try {
                    val jsr = Integer.parseInt(jsrString)
                    val response = locator.findInformation(jsr)
                    if (response != null && !response.isEmpty()) {
                        bot.postMessageToChannel(event, response)
                    } else {
                        bot.postMessageToChannel(event, Sofia.jsrUnknown(jsrString))
                    }
                } catch (nfe: NumberFormatException) {
                    bot.postMessageToChannel(event, Sofia.jsrInvalid(jsrString))
                }

                return true
            }
        }
        return false
    }
}

