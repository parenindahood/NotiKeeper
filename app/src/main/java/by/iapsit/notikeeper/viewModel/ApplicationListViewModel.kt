package by.iapsit.notikeeper.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notikeeper.model.ApplicationData
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.view.enums.ScreenState
import by.iapsit.notikeeper.viewModel.base.BaseApplicationViewModel
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
        val list = mutableListOf<String>()
        packageNames.forEach {
            if (!isSystemPackage(it)) list.add(it)
        }
        return list
    }

    private fun makeListUnique(list: List<String>) = list.distinct()

    private suspend fun checkFavourites(packageNames: List<String>): List<String> {
        val uiTask = uiScope.async {
            val ioTask = ioScope.async {
                notificationDao.getFavouritePackageNames()
            }
            val newList = packageNames.toMutableList()
            newList.removeAll(ioTask.await())
            newList
        }
        return uiTask.await()
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
                if (preferences.getBoolean(Constants.FILTER_PREF, false)) {
                    _applicationListLiveData.postValue(
                        makeListOfApplicationInfo(
                            filter(
                                makeListOfPackageNames(
                                    packageNames
                                )
                            )
                        )
                    )
                } else {
                    _applicationListLiveData.postValue(
                        makeListOfApplicationInfo(
                            makeListOfPackageNames(
                                packageNames
                            )
                        )
                    )
                }
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

    private suspend fun filter(packageNames: List<String>): List<String> {
        val uiTask = uiScope.async {
            val list = packageNames.toMutableList()
            val ioTask = ioScope.async {
                notificationDao.getAllFilteredPackageNames()
            }
            list.removeAll(ioTask.await())
            list
        }
        return uiTask.await()
    }

    override fun onCleared() {
        super.onCleared()
        packageNamesLiveData.removeObserver(packageNamesObserver)
        favouritesApplicationsLiveData.removeObserver(favouritesApplicationObserver)
    }
}