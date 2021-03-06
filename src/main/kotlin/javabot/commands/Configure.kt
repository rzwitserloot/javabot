package javabot.commands

import com.antwerkz.sofia.Sofia
import com.beust.jcommander.Parameter
import javabot.Javabot
import javabot.Message
import javabot.dao.AdminDao
import javabot.dao.ConfigDao
import org.apache.commons.lang.StringUtils
import javax.inject.Inject

class Configure @Inject constructor(bot: Javabot, adminDao: AdminDao, var configDao: ConfigDao): AdminCommand(bot, adminDao) {

    @Parameter(names = arrayOf("--property"))
    var property: String? = null
    @Parameter(names = arrayOf("--value"))
    var value: String? = null

    override fun execute(event: Message): List<Message> {
        val responses = arrayListOf<Message>()
        val config = configDao.get()
        if (StringUtils.isEmpty(property) || StringUtils.isEmpty(value)) {
            responses.add(Message(event.user, config.toString()))
        } else {
            try {
                val name = property!!.substring(0, 1).toUpperCase() + property!!.substring(1)
                val get = config.javaClass.getDeclaredMethod("get" + name)
                val type = get.returnType
                val set = config.javaClass.getDeclaredMethod("set" + name, type)
                try {
                    set.invoke(config, if (type == String::class.java) value!!.trim() else Integer.parseInt(value))
                    configDao.save(config)
                    responses.add(Message(event.user, Sofia.configurationSetProperty(property, value)))
                } catch (e: ReflectiveOperationException) {
                    responses.add(Message(event.user, e.message!!))
                } catch (e: NumberFormatException) {
                    responses.add(Message(event.user, e.message!!))
                }

            } catch (e: NoSuchMethodException) {
                responses.add(Message(event.user, Sofia.configurationUnknownProperty(property)))
            }

        }
        return responses
    }
}
