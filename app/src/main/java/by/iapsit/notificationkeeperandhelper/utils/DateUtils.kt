package by.iapsit.notificationkeeperandhelper.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_PATTERN = "dd MMMM yyyy hh:mm"

    fun formatDate(postTime: Long): String {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return format.format(postTime)
    }
}