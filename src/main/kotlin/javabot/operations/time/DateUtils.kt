package javabot.operations.time

import java.util.Date

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

public class DateUtils {
    private val dateFormatter = DateTimeFormat.forPattern("MMMM dd")
    private val timeFormatter = DateTimeFormat.forPattern("h:mm aa")

    public fun getCurrentDateAtZone(timezone: String): String {
        val date = Date().time
        val formattedDate = dateFormatter.withZone(DateTimeZone.forID(timezone)).print(date)
        val formattedTime = timeFormatter.withZone(DateTimeZone.forID(timezone)).print(date).toLowerCase()
        return formattedDate + " @ " + formattedTime
    }
}
