package by.iapsit.notikeeper.utils

import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import by.iapsit.notikeeper.model.ApplicationData
import com.google.android.material.snackbar.Snackbar

fun Activity.makeToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Activity.makeSnackBarWithAction(
    text: String,
    actionText: String,
    view: View,
    action: () -> Unit
) = Snackbar.make(this, view, text, Snackbar.LENGTH_LONG).setAction(actionText) { action.invoke() }
    .show()

fun SharedPreferences.putBoolean(tag: String, value: Boolean) =
    this.edit().putBoolean(tag, value).apply()

fun List<ApplicationInfo>.toData(packageManager: PackageManager): List<ApplicationData> {
    val list = mutableListOf<ApplicationData>()
    this.forEach {
        with(packageManager) {
            list.add(ApplicationData(
                    it.packageName,
                    getApplicationLabel(it).toString(),
                    getApplicationIcon(it)
            ))
        }
    }
    return list
}