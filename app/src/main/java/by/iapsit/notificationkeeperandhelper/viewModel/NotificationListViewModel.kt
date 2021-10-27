package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notificationkeeperandhelper.App
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.model.ApplicationData
import by.iapsit.notificationkeeperandhelper.db.entities.NotificationEntity
import by.iapsit.notificationkeeperandhelper.model.NotificationData
import by.iapsit.notificationkeeperandhelper.view.ScreenState
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
        _notificationListLiveData.postValue(list.map { it.toData() })
        _stateScreenListener.postValue(ScreenState.CONTENT)
    }

    init {
        _stateScreenListener.postValue(ScreenState.LOADING)
        notificationLiveData.observeForever(observer)
        makeApplicationInfo()
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
            with(getApplication<App>().resources) {
                _applicationInfoLiveData.postValue(
                    ApplicationData(
                        packageName,
                        packageName,
                        getDrawable(R.drawable.ic_android)
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        notificationLiveData.removeObserver(observer)
    }
}
