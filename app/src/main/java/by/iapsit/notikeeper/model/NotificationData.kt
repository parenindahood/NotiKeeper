package by.iapsit.notikeeper.model

data class NotificationData(
    val packageName: String,
    val text: String,
    val title: String,
    val postTime: Long,
    val id: Long
)