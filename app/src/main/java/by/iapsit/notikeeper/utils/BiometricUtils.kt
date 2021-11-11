package by.iapsit.notikeeper.utils

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import by.iapsit.notikeeper.R

object BiometricUtils {

    private const val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var resources: Resources

    fun canAuthenticate(context: Context) = BiometricManager.from(context)
        .canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS

    fun authorize(activity: AppCompatActivity) {
        resources = activity.resources
        biometricPrompt = getBiometricPrompt(activity)
        biometricPrompt.authenticate(createPromptInfo())
    }

    private fun getBiometricPrompt(activity: AppCompatActivity) =
        BiometricPrompt(activity, getExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        activity.finish()
                        activity.makeToast(resources.getString(R.string.authentication_error))
                    }
                }
            })

    private fun getExecutor(context: Context) = ContextCompat.getMainExecutor(context)

    private fun createPromptInfo() = BiometricPrompt.PromptInfo.Builder()
        .setTitle(resources.getString(R.string.biometric_authentication))
        .setConfirmationRequired(false)
        .setAllowedAuthenticators(authenticators)
        .build()

}