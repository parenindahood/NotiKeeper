package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.iapsit.notificationkeeperandhelper.model.ApplicationData
import by.iapsit.notificationkeeperandhelper.view.enums.ScreenState
import by.iapsit.notificationkeeperandhelper.viewModel.base.BaseApplicationViewModel
import kotlinx.coroutines.launch

class FavouritesListViewModel(application: Application) : BaseApplicationViewModel(application) {

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

    override fun onCleared() {
        super.onCleared()
        packageNamesLiveData.removeObserver(observer)
    }
}