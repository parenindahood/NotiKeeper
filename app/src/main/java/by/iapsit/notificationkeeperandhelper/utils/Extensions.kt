package by.iapsit.notificationkeeperandhelper.utils

import android.app.Activity
import android.widget.Toast

fun Activity.makeToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()