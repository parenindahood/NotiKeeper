package by.iapsit.notikeeper.viewModel.base

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import by.iapsit.notikeeper.App
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.db.entities.FavouriteApplicationEntity
import by.iapsit.notikeeper.db.entities.NotificationEntity
import by.iapsit.notikeeper.model.ApplicationData
import by.iapsit.notikeeper.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseApplicationViewModel(application: Application) : BaseViewModel(application) {

    protected val preferences: SharedPreferences =
        getApplication<App>().applicationContext.getSharedPreferences(
            Constants.SHARED_PREF_TITLE,
            AppCompatActivity.MODE_PRIVATE
        )

    fun insertFavouriteApplication(favouriteApplication: FavouriteApplicationEntity) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.insertFavouritePackageName(favouriteApplication)
            }
        }
    }

    fun deleteFavouriteApplication(packageName: String) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.deleteFavouritePackageName(packageName)
            }
        }
    }

    fun deleteNotificationsByPackageName(packageName: String) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.deleteNotificationsByPackageName(packageName)
            }
        }
    }

    fun undoDeleteApplication(list: List<NotificationEntity>) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.insertNotificationList(list)
            }
        }
    }

    suspend fun getNotificationsByPackageName(packageName: String): List<NotificationEntity> {
        val firstTask = uiScope.async {
            val secondTask = ioScope.async {
                notificationDao.getNotificationsByPackageName(packageName)
            }
            secondTask.await()
        }
        return firstTask.await()
    }

    protected fun makeListOfApplicationInfo(packageNames: List<String>): List<ApplicationData> {
        val applicationList = mutableListOf<ApplicationData>()
        packageNames.forEach {
            try {
                with(packageManager) {
                    val applicationInfo = getApplicationInfo(it, 0)
                    applicationList.add(
                        ApplicationData(
                            it,
                            getApplicationLabel(applicationInfo).toString(),
                            getApplicationIcon(it)
                        )
                    )
                }
            } catch (e: PackageManager.NameNotFoundException) {
                if (!preferences.getBoolean(Constants.HIDE_DELETED_PREF, false)) applicationList.add(
                    ApplicationData(
                        it,
                        it,
                        ResourcesCompat.getDrawable(
                            getApplication<App>().resources,
                            R.drawable.ic_android,
                            null
                        )!!
                    )
                )
            }
        }
        return applicationList
    }
}