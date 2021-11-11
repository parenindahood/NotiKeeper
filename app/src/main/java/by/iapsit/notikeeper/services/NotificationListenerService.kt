package by.iapsit.notikeeper.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import by.iapsit.notikeeper.App
import by.iapsit.notikeeper.db.entities.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {

    private val notificationDao by lazy { (application as App).database.getNotificationDao() }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val extras = sbn?.notification?.extras
        if (sbn != null && extras != null) {
            uiScope.launch {
                ioScope.launch {
                    insertNotification(
                        sbn.packageName,
                        extras.getString(Notification.EXTRA_TEXT),
                        extras.getString(Notification.EXTRA_TITLE),
                        sbn.postTime
                    )
                }
            }
        }
    }

    private fun insertNotification(
        packageName: String,
        text: String?,
        title: String?,
        postTime: Long
    ) {
        if (text != null && title != null) notificationDao.insertNotification(
            NotificationEntity(
                packageName,
                text,
                title,
                postTime
            )
        )
    }
}