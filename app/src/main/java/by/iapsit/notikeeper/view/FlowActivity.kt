package by.iapsit.notikeeper.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.databinding.ActivityFlowBinding
import by.iapsit.notikeeper.utils.BiometricUtils
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.utils.makeToast
import by.iapsit.notikeeper.workers.DeleteDataWorker
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class FlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding

    val preferences by inject<SharedPreferences> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startWorkerForDeleting()

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

    private fun convertDpToPixels(dp: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBottomNavigationView() {
        with(binding) {
            navView.visibility = View.VISIBLE
            val layoutParams = ConstraintLayout.LayoutParams(navHostFragment.layoutParams)
            layoutParams.bottomMargin = convertDpToPixels(60f)
            navHostFragment.layoutParams = layoutParams
        }
    }

    private fun hideBottomNavigationView() {
        with(binding) {
            navView.visibility = View.GONE
            val layoutParams = ConstraintLayout.LayoutParams(navHostFragment.layoutParams)
            layoutParams.bottomMargin = convertDpToPixels(0f)
            navHostFragment.layoutParams = layoutParams
        }
    }

    private fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun hideBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    fun showNotificationsUI() {
        hideBottomNavigationView()
        showBackButton()
    }

    fun hideNotificationsUI() {
        showBottomNavigationView()
        hideBackButton()
    }

    private fun startWorkerForDeleting() {
        val workRequest = PeriodicWorkRequestBuilder<DeleteDataWorker>(
            Constants.DELETING_INTERVAL,
            TimeUnit.HOURS,
            Constants.FLEX_TIME_INTERVAL,
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            Constants.DELETING_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}