package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notificationkeeperandhelper.App
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.model.ApplicationData
import by.iapsit.notificationkeeperandhelper.db.entities.NotificationEntity
import by.iapsit.notificationkeeperandhelper.db.entities.toEntity
import by.iapsit.notificationkeeperandhelper.model.NotificationData
import by.iapsit.notificationkeeperandhelper.view.enums.ScreenState
import by.iapsit.notificationkeeperandhelper.viewModel.base.BaseViewModel
import kotlinx.coroutines.launch

class NotificationListViewModel(application: Application, val packageName: String) :
    BaseViewModel(application) {

    private val _notificationListLiveData = MutableLiveData<List<NotificationData>>()

    val notificationListLiveData: LiveData<List<NotificationData>>
        get() = _notificationListLiveData

    private val notificationLiveData =
        notificationDao.getNotificationsByPackageNameLiveData(packageName)

    private val _applicationInfoLiveData = MutableLiveData<ApplicationData>()

    val applicationInfoLiveData: LiveData<ApplicationData>
        get() = _applicationInfoLiveData

    private val observer = Observer<List<NotificationEntity>> { list ->
        _notificationListLiveData.postValue(list.map { it.toData() }.sortedBy { it.postTime })
        _stateScreenListener.postValue(ScreenState.CONTENT)
    }

    init {
        _stateScreenListener.postValue(ScreenState.LOADING)
        observeLiveData()
        makeApplicationInfo()
    }

    fun observeLiveData() {
        notificationLiveData.observeForever(observer)
    }

    fun removeObserverLiveData() {
        notificationLiveData.removeObserver(observer)
    }

    fun deleteNotificationByID(id: Long) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.deleteNotificationByID(id)
            }
        }
    }

    private fun makeApplicationInfo() {
        val packageManager = getApplication<App>().applicationContext.packageManager
        try {
            with(packageManager) {
                val applicationInfo = getApplicationInfo(packageName, 0)
                _applicationInfoLiveData.postValue(
                    ApplicationData(
                        packageName,
                        getApplicationLabel(applicationInfo).toString(),
                        getApplicationIcon(packageName)
                    )
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            _applicationInfoLiveData.postValue(
                ApplicationData(
                    packageName,
                    packageName,
                    ResourcesCompat.getDrawable(
                        getApplication<App>().resources,
                        R.drawable.ic_android,
                        null
                    )!!
                )
            )
        }
    }

    fun undoDeleteNotification(notification: NotificationData) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.insertNotification(notification.toEntity())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeObserverLiveData()
    }
}
