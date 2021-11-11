package by.iapsit.notikeeper.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import by.iapsit.notikeeper.adapters.ApplicationListAdapter
import by.iapsit.notikeeper.adapters.NotificationListAdapter
import by.iapsit.notikeeper.db.NotificationDatabase
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.viewModel.ApplicationListViewModel
import by.iapsit.notikeeper.viewModel.FavouritesListViewModel
import by.iapsit.notikeeper.viewModel.NotificationListViewModel
import by.iapsit.notikeeper.viewModel.SettingsViewModel
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
}

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(), NotificationDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }
}