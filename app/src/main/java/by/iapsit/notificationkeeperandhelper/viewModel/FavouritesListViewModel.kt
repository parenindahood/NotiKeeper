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
import by.iapsit.notificationkeeperandhelper.view.ScreenState
import kotlinx.coroutines.launch

class FavouritesListViewModel(application: Application) : BaseViewModel(application) {

    private val _applicationListLiveData = MutableLiveData<List<ApplicationData>>()

    val applicationListLiveData: LiveData<List<ApplicationData>>
        get() = _applicationListLiveData

    private val packageNamesLiveData = notificationDao.getFavouritePackageNamesLiveData()

    private val observer = Observer<List<String>> {
        if (it.isEmpty()) {
            _stateScreenListener.postValue(ScreenState.ERROR)
        } else {
            uiScope.launch {
                _applicationListLiveData.postValue(makeListOfApplicationInfo(it))
                _stateScreenListener.postValue(ScreenState.CONTENT)
            }
        }
    }

    init {
        _stateScreenListener.postValue(ScreenState.LOADING)
        packageNamesLiveData.observeForever(observer)
    }

    private fun makeListOfApplicationInfo(packageNames: List<String>): List<ApplicationData> {
        val packageManager = getApplication<App>().applicationContext.packageManager
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
                applicationList.add(
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

    override fun onCleared() {
        super.onCleared()
        packageNamesLiveData.removeObserver(observer)
    }
}