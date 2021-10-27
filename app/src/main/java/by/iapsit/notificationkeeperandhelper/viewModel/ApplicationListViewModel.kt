package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notificationkeeperandhelper.App
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.db.entities.FavouriteApplicationEntity
import by.iapsit.notificationkeeperandhelper.model.ApplicationData
import by.iapsit.notificationkeeperandhelper.utils.Constants
import by.iapsit.notificationkeeperandhelper.view.ScreenState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ApplicationListViewModel(application: Application) : BaseViewModel(application) {

    private val preferences = getApplication<App>().applicationContext.getSharedPreferences(
        Constants.SHARED_PREF_TITLE,
        AppCompatActivity.MODE_PRIVATE
    )

    private val _applicationListLiveData = MutableLiveData<List<ApplicationData>>()

    val applicationListLiveData: LiveData<List<ApplicationData>>
        get() = _applicationListLiveData

    private val packageNamesLiveData = notificationDao.getPackageNamesLiveData()

    private val favouritesApplicationsLiveData = notificationDao.getFavouritePackageNamesLiveData()

    private val packageNamesObserver = Observer<List<String>> {
        updateLiveData(it)
    }

    private val favouritesApplicationObserver = Observer<List<String>> {
        updateUI()
    }

    init {
        _stateScreenListener.postValue(ScreenState.LOADING)
        packageNamesLiveData.observeForever(packageNamesObserver)
        favouritesApplicationsLiveData.observeForever(favouritesApplicationObserver)
    }

    private suspend fun makeListOfApplicationInfo(packageNames: List<String>): List<ApplicationData> {
        val packageManager = getApplication<App>().applicationContext.packageManager
        val applicationList = mutableListOf<ApplicationData>()
        val list = if (preferences.getBoolean(Constants.HIDE_SYSTEM_PREF, false))
            checkFavourites(
                makeListUnique(
                    checkSystem(
                        packageNames
                    )
                )
            )
        else checkFavourites(makeListUnique(packageNames))
        list.forEach {
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
                if (!preferences.getBoolean(Constants.HIDE_DELETED_PREF, true)) applicationList.add(
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

    private fun checkSystem(packageNames: List<String>): List<String> {
        val newList = packageNames.toMutableList().apply {
            Constants.SYSTEM_PACKAGES.forEach { packageName: String ->
                removeAll {
                    it.startsWith(packageName)
                }
            }
        }
        return newList
    }

    private fun makeListUnique(list: List<String>) = list.distinct()

    fun insertFavouriteApplication(favouriteApplication: FavouriteApplicationEntity) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.insertFavouritePackageName(favouriteApplication)
            }
        }
    }

    private suspend fun checkFavourites(packageNames: List<String>): List<String> {
        val returnTask = uiScope.async {
            val task = ioScope.async {
                notificationDao.getFavouritePackageNames()
            }
            val newList = packageNames.toMutableList()
            newList.removeAll(task.await())
            newList
        }
        return returnTask.await()
    }

    private fun updateUI() {
        uiScope.launch {
            val task = ioScope.async {
                notificationDao.getPackageNames()
            }
            updateLiveData(task.await())
        }
    }

    private fun updateLiveData(packageNames: List<String>) {
        if (packageNames.isEmpty()) {
            _stateScreenListener.postValue(ScreenState.ERROR)
        } else {
            uiScope.launch {
                _applicationListLiveData.postValue(makeListOfApplicationInfo(packageNames))
                _stateScreenListener.postValue(ScreenState.CONTENT)
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
        packageNamesLiveData.removeObserver(packageNamesObserver)
        favouritesApplicationsLiveData.removeObserver(favouritesApplicationObserver)
    }
}