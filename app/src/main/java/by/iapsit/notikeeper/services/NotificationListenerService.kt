package by.iapsit.notikeeper.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import by.iapsit.notikeeper.App
import by.iapsit.notikeeper.db.entities.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {

    private val notificationDao by lazy { (application as App).database.getNotificationDao() }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val extras = sbn?.notification?.extras
        if (sbn != null && extras != null) {

            val packageName = sbn.packageName
            val text = extras.getString(Notification.EXTRA_TEXT)
            val title = extras.getString(Notification.EXTRA_TITLE)
            val postTime = sbn.postTime

            if (text != null && title != null) {
                uiScope.launch {
                    ioScope.launch {
                        if (!notificationDao.checkNotificationExists(
                                packageName, text, title, postTime
                            )
                        ) {
                            notificationDao.insertNotification(
                                NotificationEntity(
                                    packageName,
                                    text,
                                    title,
                                    postTime
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
        ioScope.cancel()
    }
}