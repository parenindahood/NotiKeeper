package by.iapsit.notikeeper.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.iapsit.notikeeper.db.entities.FavouriteApplicationEntity
import by.iapsit.notikeeper.db.entities.FilteredApplicationEntity
import by.iapsit.notikeeper.db.entities.NotificationEntity

@Dao
interface NotificationDao {

    @Insert
    fun insertNotification(notification: NotificationEntity)

    @Insert
    fun insertNotificationList(list: List<NotificationEntity>)

    @Query("SELECT DISTINCT package_name FROM NotificationEntity WHERE package_name NOT IN (SELECT package_name FROM FavouriteApplicationEntity) AND is_deleted = 0")
    fun getPackageNamesLiveData(): LiveData<List<String>>

    @Query("SELECT * FROM NotificationEntity WHERE package_name LIKE :packageName AND is_deleted = 0 ORDER BY post_time")
    fun getNotificationsByPackageNameLiveData(packageName: String): LiveData<List<NotificationEntity>>

    @Query("SELECT * FROM NotificationEntity WHERE package_name LIKE :packageName")
    fun getNotificationsByPackageName(packageName: String): List<NotificationEntity>

    @Query("DELETE FROM NotificationEntity WHERE package_name LIKE :packageName")
    fun deleteNotificationsByPackageName(packageName: String)

    @Query("DELETE FROM NotificationEntity")
    fun deleteAllNotifications()

    @Insert
    fun insertFavouritePackageName(application: FavouriteApplicationEntity)

    @Query("SELECT package_name FROM FavouriteApplicationEntity")
    fun getFavouritePackageNamesLiveData(): LiveData<List<String>>

    @Query("DELETE FROM FavouriteApplicationEntity WHERE package_name LIKE :packageName")
    fun deleteFavouritePackageName(packageName: String)

    @Query("DELETE FROM FavouriteApplicationEntity")
    fun deleteAllFavouritePackageNames()

    @Query("SELECT EXISTS(SELECT * FROM NotificationEntity WHERE package_name LIKE :packageName AND text LIKE :text AND title LIKE :title AND :postTime - post_time < 1000)")
    fun checkNotificationExists(
        packageName: String,
        text: String,
        title: String,
        postTime: Long
    ): Boolean

    @Insert
    fun insertFilteredPackageName(filteredApplication: FilteredApplicationEntity)

    @Query("DELETE FROM FilteredApplicationEntity WHERE package_name LIKE :packageName")
    fun deleteFilteredPackageName(packageName: String)

    @Query("SELECT EXISTS(SELECT * FROM FilteredApplicationEntity WHERE package_name LIKE :packageName)")
    fun checkIsFiltered(packageName: String): Boolean

    @Query("SELECT DISTINCT package_name FROM NotificationEntity WHERE package_name NOT IN (SELECT package_name FROM FavouriteApplicationEntity) AND package_name NOT IN (SELECT package_name FROM FilteredApplicationEntity)")
    fun getFilteredPackageNamesLiveData(): LiveData<List<String>>

    @Query("UPDATE NotificationEntity SET is_deleted = :isDeleted WHERE package_name LIKE :packageName")
    fun softDeleteNotifications(packageName: String, isDeleted: Boolean)

    @Query("DELETE FROM NotificationEntity WHERE is_deleted LIKE 1")
    fun deleteAllSoftDeletedNotifications()

    @Query("UPDATE NotificationEntity SET is_deleted = 0")
    fun cancelSoftDeleting()

    @Query("UPDATE NotificationEntity SET is_deleted = :isDeleted WHERE notification_id LIKE :id")
    fun softDeleteNotificationByID(id: Long, isDeleted: Boolean)
}