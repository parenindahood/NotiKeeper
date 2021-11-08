package by.iapsit.notificationkeeperandhelper.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import by.iapsit.notificationkeeperandhelper.R

class DeleteDataConfirmationDialogFragment : DialogFragment() {

    lateinit var listener: Listener

    companion object {
        const val TAG = "DeleteDataConfirmationDialog"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$parentFragment must implement DeleteDataConfirmationDialogFragment.Listener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        with(resources) {
            return AlertDialog.Builder(activity)
                .setMessage(getString(R.string.delete_all_data_question))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    listener.onDialogPositiveClick(this@DeleteDataConfirmationDialogFragment)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    listener.onDialogNegativeClick(this@DeleteDataConfirmationDialogFragment
                    )
                }
                .create()
        }
    }

    interface Listener {

        fun onDialogPositiveClick(dialog: DialogFragment)

        fun onDialogNegativeClick(dialog: DialogFragment)
    }
}