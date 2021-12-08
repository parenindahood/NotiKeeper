package by.iapsit.notikeeper.viewModel.base

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.iapsit.notikeeper.App
import by.iapsit.notikeeper.view.enums.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val notificationDao by lazy { getApplication<App>().database.getNotificationDao() }

    protected val _stateScreenListener = MutableLiveData(ScreenState.LOADING)

    protected val packageManager = getApplication<App>().packageManager

    val stateScreenListener: LiveData<ScreenState>
        get() = _stateScreenListener

    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
        ioScope.cancel()
    }

    protected fun isSystemPackage(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(
                packageName,
                0
            ).applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}