package by.iapsit.notikeeper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import by.iapsit.notikeeper.App
import by.iapsit.notikeeper.R

class DeleteDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    private val dao = (applicationContext as App).database.getNotificationDao()

    override suspend fun doWork(): Result {
        dao.deleteAllSoftDeletedNotifications()
        return Result.success()
    }
}