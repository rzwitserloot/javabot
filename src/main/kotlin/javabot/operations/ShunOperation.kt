package javabot.operations

import com.antwerkz.sofia.Sofia
import javabot.Message
import javabot.dao.ShunDao

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

/**
 * Causes the bot to disregard bot triggers for a few minutes. Useful to de-fang abusive users without ejecting the bot from a channel
 * entirely.
 */
public class ShunOperation : BotOperation() {
    Inject
    private val shunDao: ShunDao? = null

    override fun handleMessage(event: Message): Boolean {
        val message = event.value
        if (message.startsWith("shun ")) {
            val parts = message.substring(5).split(" ")
            if (parts.size() == 0) {
                bot.postMessageToChannel(event, Sofia.shunUsage())
            } else {
                bot.postMessageToChannel(event, getShunnedMessage(parts))
            }
            return true
        }
        return false
    }

    private fun getShunnedMessage(parts: Array<String>): String {
        val victim = parts[0]
        if (shunDao!!.isShunned(victim)) {
            return Sofia.alreadyShunned(victim)
        }
        val until = if (parts.size() == 1)
            LocalDateTime.now().plusMinutes(5)
        else
            LocalDateTime.now().plusSeconds(Integer.parseInt(parts[1]).toLong())
        shunDao.addShun(victim, until)

        return String.format(Sofia.shunned(victim, Date(until.toEpochSecond(ZoneOffset.UTC))))
    }
}