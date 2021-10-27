package by.iapsit.notificationkeeperandhelper.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.iapsit.notificationkeeperandhelper.model.NotificationData

@Entity
data class NotificationEntity(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "post_time") val postTime: String,
    @ColumnInfo(name = "notification_id") @PrimaryKey(autoGenerate = true) val notificationID: Long = 0
) { fun toData() = NotificationData(packageName, text, title, postTime, notificationID) }
