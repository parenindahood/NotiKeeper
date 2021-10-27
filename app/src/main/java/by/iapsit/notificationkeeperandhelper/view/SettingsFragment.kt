package by.iapsit.notificationkeeperandhelper.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.databinding.FragmentSettingsBinding
import by.iapsit.notificationkeeperandhelper.utils.BiometricUtils
import by.iapsit.notificationkeeperandhelper.utils.Constants

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        preferences = (requireActivity() as FlowActivity).preferences

        setSwitchesOnCheckListener()
        setUI()

        return binding.root
    }

    private fun setUI() {
        with(binding) {
            securitySwitch.isEnabled = BiometricUtils.canAuthenticate(requireContext())
            with (preferences) {
                securitySwitch.isChecked = getBoolean(Constants.SECURITY_PREF, false)
                hideSystemSwitch.isChecked = getBoolean(Constants.HIDE_SYSTEM_PREF, false)
                hideDeletedSwitch.isChecked = getBoolean(Constants.HIDE_DELETED_PREF, false)
            }
        }
    }

    private fun setSwitchesOnCheckListener() {
        with(binding) {
            securitySwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean(Constants.SECURITY_PREF, isChecked).apply()
            }
            hideSystemSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean(Constants.HIDE_SYSTEM_PREF, isChecked).apply()
            }
            hideDeletedSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean(Constants.HIDE_DELETED_PREF, isChecked).apply()
            }
        }
    }
}