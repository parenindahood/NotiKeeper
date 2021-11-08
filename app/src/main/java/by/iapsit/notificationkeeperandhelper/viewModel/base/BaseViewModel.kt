package by.iapsit.notificationkeeperandhelper.viewModel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.iapsit.notificationkeeperandhelper.App
import by.iapsit.notificationkeeperandhelper.view.enums.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected val notificationDao by lazy { getApplication<App>().database.getNotificationDao() }

    protected val _stateScreenListener = MutableLiveData(ScreenState.LOADING)

    val stateScreenListener: LiveData<ScreenState>
        get() = _stateScreenListener

    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
        ioScope.cancel()
    }
}