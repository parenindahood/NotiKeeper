package by.iapsit.notikeeper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import by.iapsit.notikeeper.db.NotificationDatabase
import by.iapsit.notikeeper.di.*
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
                    databaseModule,
                    vibratorModule
                )
            )
        }
    }
}