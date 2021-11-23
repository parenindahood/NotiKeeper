package by.iapsit.notikeeper.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.databinding.FragmentSettingsBinding
import by.iapsit.notikeeper.utils.BiometricUtils
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.utils.putBoolean
import by.iapsit.notikeeper.view.FlowActivity
import by.iapsit.notikeeper.view.dialog.DeleteDataConfirmationDialogFragment
import by.iapsit.notikeeper.viewModel.SettingsViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(),
    DeleteDataConfirmationDialogFragment.OnDialogButtonsClickListener {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var preferences: SharedPreferences

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        preferences = (requireActivity() as FlowActivity).preferences

        setButtonsOnClickListeners()
        setSwitchesOnCheckListeners()
        setUI()

        return binding.root
    }

    private fun setButtonsOnClickListeners() {
        with(binding) {
            deleteAllDataButton.setOnClickListener {
                DeleteDataConfirmationDialogFragment().show(
                    childFragmentManager, DeleteDataConfirmationDialogFragment.TAG
                )
            }
            filterButton.setOnClickListener {
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsToFilter()
                )
            }
        }
    }

    private fun setUI() {
        with(binding) {
            securitySwitch.isEnabled = BiometricUtils.canAuthenticate(requireContext())
            with(preferences) {
                securitySwitch.isChecked = getBoolean(Constants.SECURITY_PREF, false)
                hideSystemSwitch.isChecked = getBoolean(Constants.HIDE_SYSTEM_PREF, false)
                hideDeletedSwitch.isChecked = getBoolean(Constants.HIDE_DELETED_PREF, false)
                filterSwitch.isChecked = getBoolean(Constants.FILTER_PREF, false)
                filterButton.isEnabled = getBoolean(Constants.FILTER_PREF, false)
            }
        }
    }

    private fun setSwitchesOnCheckListeners() {
        with(binding) {
            securitySwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.putBoolean(Constants.SECURITY_PREF, isChecked)
            }
            hideSystemSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.putBoolean(Constants.HIDE_SYSTEM_PREF, isChecked)
            }
            hideDeletedSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.putBoolean(Constants.HIDE_DELETED_PREF, isChecked)
            }
            filterSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.putBoolean(Constants.FILTER_PREF, isChecked)
                filterButton.isEnabled = isChecked
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) = viewModel.deleteAllData()

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
}