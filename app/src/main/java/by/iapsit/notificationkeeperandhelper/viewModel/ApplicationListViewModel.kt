package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notificationkeeperandhelper.model.ApplicationData
import by.iapsit.notificationkeeperandhelper.utils.Constants
import by.iapsit.notificationkeeperandhelper.view.enums.ScreenState
import by.iapsit.notificationkeeperandhelper.viewModel.base.BaseApplicationViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ApplicationListViewModel(application: Application) : BaseApplicationViewModel(application) {

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
                _applicationListLiveData.postValue(
                    makeListOfApplicationInfo(
                        makeListOfPackageNames(
                            packageNames
                        )
                    )
                )
                _stateScreenListener.postValue(ScreenState.CONTENT)
            }
        }
    }

    private suspend fun makeListOfPackageNames(packageNames: List<String>) =
        if (preferences.getBoolean(Constants.HIDE_SYSTEM_PREF, false))
            checkFavourites(
                makeListUnique(
                    checkSystem(
                        packageNames
                    )
                )
            )
        else checkFavourites(makeListUnique(packageNames))

    override fun onCleared() {
        super.onCleared()
        packageNamesLiveData.removeObserver(packageNamesObserver)
        favouritesApplicationsLiveData.removeObserver(favouritesApplicationObserver)
    }
}