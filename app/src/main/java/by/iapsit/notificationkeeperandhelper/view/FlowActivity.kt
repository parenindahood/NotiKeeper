package by.iapsit.notificationkeeperandhelper.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.databinding.ActivityFlowBinding
import by.iapsit.notificationkeeperandhelper.utils.BiometricUtils
import by.iapsit.notificationkeeperandhelper.utils.Constants
import by.iapsit.notificationkeeperandhelper.utils.makeToast
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding

    val preferences by inject<SharedPreferences> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authorize()
    }

    private fun authorize() {
        if (preferences.getBoolean(Constants.SECURITY_PREF, false)) BiometricUtils.authorize(
            this
        )
    }

    override fun onStart() {
        super.onStart()
        checkNotificationAccess()
        binding.navView.setupWithNavController(findNavController(binding.navHostFragment.id))
    }

    private fun checkNotificationAccess() {
        if (!checkEnabledListeners()) startNotificationSettingsActivity()
    }

    private fun checkEnabledListeners() =
        NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName)

    private fun startNotificationSettingsActivity() {
        makeToast(resources.getString(R.string.check_access_toast))
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}