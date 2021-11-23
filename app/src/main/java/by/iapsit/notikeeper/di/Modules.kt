package by.iapsit.notikeeper.di

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import by.iapsit.notikeeper.adapters.ApplicationFilterAdapter
import by.iapsit.notikeeper.adapters.ApplicationListAdapter
import by.iapsit.notikeeper.adapters.NotificationListAdapter
import by.iapsit.notikeeper.db.NotificationDatabase
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.viewModel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { ApplicationListViewModel(androidApplication()) }
    viewModel { FavouritesListViewModel(androidApplication()) }
    viewModel { (packageName: String) ->
        NotificationListViewModel(
            androidApplication(),
            packageName
        )
    }
    viewModel { SettingsViewModel(androidApplication()) }
    viewModel { FilterViewModel(androidApplication()) }
}

val sharedPreferencesModule = module {

    factory { (context: Context) ->
        context.getSharedPreferences(
            Constants.SHARED_PREF_TITLE,
            AppCompatActivity.MODE_PRIVATE
        )
    }
}

val adaptersModule = module {

    factory { (openNotificationListAction: (String) -> Unit, setApplicationFavouriteAction: (String) -> Unit, isFavourite: Boolean) ->
        ApplicationListAdapter(
            openNotificationListAction,
            setApplicationFavouriteAction,
            isFavourite
        )
    }
    factory { NotificationListAdapter() }
    factory { (insertFilteredAction: (String) -> Unit, deleteFilteredAction: (String) -> Unit, checkIsFilteredAction: suspend (String) -> Boolean) ->
        ApplicationFilterAdapter(insertFilteredAction, deleteFilteredAction, checkIsFilteredAction)
    }
}

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(), NotificationDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }
}

val vibratorModule = module {

    factory { (context: Context) ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}