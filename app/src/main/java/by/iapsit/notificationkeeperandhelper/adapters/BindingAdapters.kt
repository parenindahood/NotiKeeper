package by.iapsit.notificationkeeperandhelper.adapters

import android.widget.TextView
import android.widget.ViewFlipper
import androidx.databinding.BindingAdapter
import by.iapsit.notificationkeeperandhelper.utils.DateUtils
import by.iapsit.notificationkeeperandhelper.view.enums.ScreenState

@BindingAdapter("displayed_child")
fun setViewFlipperDisplayedChild(viewFlipper: ViewFlipper, screenState: ScreenState?) {
    viewFlipper.displayedChild = screenState?.viewIndex ?: ScreenState.ERROR.viewIndex
}

@BindingAdapter("app:format_date")
fun setFormatDate(view: TextView, time: Long) {
    view.text = DateUtils.formatDate(time)
}