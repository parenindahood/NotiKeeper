package by.iapsit.notikeeper.db

import androidx.room.Database
import androidx.room.RoomDatabase
import by.iapsit.notikeeper.db.entities.FavouriteApplicationEntity
import by.iapsit.notikeeper.db.entities.NotificationEntity

@Database(
    entities = [NotificationEntity::class, FavouriteApplicationEntity::class], version = 1
)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun getNotificationDao(): NotificationDao
}