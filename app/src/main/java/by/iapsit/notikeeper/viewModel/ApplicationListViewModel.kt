package by.iapsit.notikeeper.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notikeeper.model.ApplicationData
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.view.enums.ScreenState
import by.iapsit.notikeeper.viewModel.base.BaseApplicationViewModel
import kotlinx.coroutines.launch

class ApplicationListViewModel(application: Application) : BaseApplicationViewModel(application) {

    private val _applicationListLiveData = MutableLiveData<List<ApplicationData>>()

    val applicationListLiveData: LiveData<List<ApplicationData>>
        get() = _applicationListLiveData

    private val packageNamesLiveData = if (preferences.getBoolean(
            Constants.FILTER_PREF,
            false
        )
    ) notificationDao.getFilteredPackageNamesLiveData() else notificationDao.getPackageNamesLiveData()

    private val packageNamesObserver = Observer<List<String>> {
        updateLiveData(it)
    }

    init {
        _stateScreenListener.postValue(ScreenState.LOADING)
        packageNamesLiveData.observeForever(packageNamesObserver)
    }

    private fun checkSystem(packageNames: List<String>): List<String> {
        val list = mutableListOf<String>()
        packageNames.forEach {
            if (!isSystemPackage(it)) list.add(it)
        }
        return list
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

    private fun makeListOfPackageNames(packageNames: List<String>) =
        if (preferences.getBoolean(Constants.HIDE_SYSTEM_PREF, false)) checkSystem(packageNames)
        else packageNames


    override fun onCleared() {
        super.onCleared()
        packageNamesLiveData.removeObserver(packageNamesObserver)
    }
}