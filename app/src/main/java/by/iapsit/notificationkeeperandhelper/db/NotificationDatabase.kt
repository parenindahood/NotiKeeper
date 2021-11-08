package by.iapsit.notificationkeeperandhelper.db

import androidx.room.Database
import androidx.room.RoomDatabase
import by.iapsit.notificationkeeperandhelper.db.entities.FavouriteApplicationEntity
import by.iapsit.notificationkeeperandhelper.db.entities.NotificationEntity

@Database(
    entities = [NotificationEntity::class, FavouriteApplicationEntity::class], version = 1
)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun getNotificationDao(): NotificationDao
}