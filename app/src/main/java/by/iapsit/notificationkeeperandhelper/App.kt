package by.iapsit.notificationkeeperandhelper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import by.iapsit.notificationkeeperandhelper.db.NotificationDatabase
import by.iapsit.notificationkeeperandhelper.di.adaptersModule
import by.iapsit.notificationkeeperandhelper.di.databaseModule
import by.iapsit.notificationkeeperandhelper.di.sharedPreferencesModule
import by.iapsit.notificationkeeperandhelper.di.viewModelsModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    val database by inject<NotificationDatabase>()

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    viewModelsModule,
                    sharedPreferencesModule,
                    adaptersModule,
                    databaseModule
                )
            )
        }
    }
}