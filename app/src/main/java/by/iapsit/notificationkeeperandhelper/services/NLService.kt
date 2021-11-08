package by.iapsit.notificationkeeperandhelper.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import by.iapsit.notificationkeeperandhelper.App
import by.iapsit.notificationkeeperandhelper.db.entities.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NLService : NotificationListenerService() {

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

    private fun insertNotification(packageName: String, text: String?, title: String?, postTime: Long) {
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