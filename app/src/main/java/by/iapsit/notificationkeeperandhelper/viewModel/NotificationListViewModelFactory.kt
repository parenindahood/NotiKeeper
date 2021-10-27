package by.iapsit.notificationkeeperandhelper.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class NotificationListViewModelFactory(
    private val application: Application,
    private val packageName: String
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationListViewModel::class.java)) {
            return NotificationListViewModel(application, packageName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}