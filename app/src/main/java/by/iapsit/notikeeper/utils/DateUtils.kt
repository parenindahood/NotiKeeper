package by.iapsit.notikeeper.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_PATTERN = "dd MMMM yyyy HH:mm"

    fun formatDate(postTime: Long): String {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return format.format(postTime)
    }
}