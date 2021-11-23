package by.iapsit.notikeeper.viewModel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.iapsit.notikeeper.db.entities.FilteredApplicationEntity
import by.iapsit.notikeeper.model.ApplicationData
import by.iapsit.notikeeper.utils.toData
import by.iapsit.notikeeper.view.enums.ScreenState
import by.iapsit.notikeeper.viewModel.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FilterViewModel(application: Application) : BaseViewModel(application) {

    private val _applicationListLiveData = MutableLiveData<List<ApplicationData>>()

    val applicationListLiveData: LiveData<List<ApplicationData>>
        get() = _applicationListLiveData

    init {
        uiScope.launch {
            _stateScreenListener.postValue(ScreenState.LOADING)
            _applicationListLiveData.postValue(getInstalledApplications())
        }
    }

    private suspend fun getInstalledApplications(): List<ApplicationData> {
        val uiTask = uiScope.async {
            val ioTask = ioScope.async {
                val list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                val newList = mutableListOf<ApplicationInfo>()
                list.forEach {
                    if (!isSystemPackage(it.packageName)) newList.add(it)
                }
                _stateScreenListener.postValue(ScreenState.CONTENT)
                newList
            }
            ioTask.await()
        }
        return uiTask.await().toData(packageManager)
    }

    fun insertFilteredPackageName(packageName: String) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.insertFilteredPackageName(FilteredApplicationEntity(packageName))
            }
        }
    }

    fun deleteFilteredPackageName(packageName: String) {
        uiScope.launch {
            ioScope.launch {
                notificationDao.deleteFilteredPackageName(packageName)
            }
        }
    }

    suspend fun checkIsFiltered(packageName: String): Boolean {
        val uiTask = uiScope.async {
            val ioTask = ioScope.async {
                notificationDao.checkIsFiltered(packageName)
            }
            ioTask.await()
        }
        return uiTask.await()
    }
}