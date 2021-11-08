package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import by.iapsit.notificationkeeperandhelper.viewModel.base.BaseViewModel
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
}