package by.iapsit.notikeeper.viewModel

import android.app.Application
import by.iapsit.notikeeper.viewModel.base.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : BaseViewModel(application) {

    fun deleteAllData() {
        uiScope.launch {
            ioScope.launch {
                notificationDao.deleteAllFavouritePackageNames()
                notificationDao.deleteAllNotifications()
            }
        }
    }

    fun returnData() {
        uiScope.launch {
            ioScope.launch {
                notificationDao.cancelSoftDeleting()
            }
        }
    }
}