package by.iapsit.notificationkeeperandhelper.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.iapsit.notificationkeeperandhelper.db.entities.FavouriteApplicationEntity
import by.iapsit.notificationkeeperandhelper.db.entities.NotificationEntity

@Dao
interface NotificationDao {

    @Insert
    fun insertNotification(notification: NotificationEntity)

    @Query("SELECT package_name FROM NotificationEntity")
    fun getPackageNamesLiveData(): LiveData<List<String>>

    @Query("SELECT package_name FROM NotificationEntity")
    fun getPackageNames(): List<String>

    @Query("SELECT * FROM NotificationEntity WHERE package_name LIKE :packageName")
    fun getNotificationsByPackageNameLiveData(packageName: String): LiveData<List<NotificationEntity>>

    @Query("DELETE FROM NotificationEntity WHERE package_name LIKE :packageName")
    fun deleteNotificationsByPackageName(packageName: String)

    @Query("DELETE FROM NotificationEntity WHERE notification_id LIKE :id")
    fun deleteNotificationByID(id: Long)

    @Query("DELETE FROM NotificationEntity ")
    fun deleteAllNotifications()

    @Insert
    fun insertFavouritePackageName(application: FavouriteApplicationEntity)

    @Query("SELECT package_name FROM FavouriteApplicationEntity")
    fun getFavouritePackageNamesLiveData(): LiveData<List<String>>

    @Query("SELECT package_name FROM FavouriteApplicationEntity")
    fun getFavouritePackageNames(): List<String>

    @Query("DELETE FROM FavouriteApplicationEntity WHERE package_name LIKE :packageName")
    fun deleteFavouritePackageName(packageName: String)

    @Query("DELETE FROM FavouriteApplicationEntity")
    fun deleteAllFavouritePackageNames()
}