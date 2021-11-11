package by.iapsit.notikeeper.adapters

import android.widget.TextView
import android.widget.ViewFlipper
import androidx.databinding.BindingAdapter
import by.iapsit.notikeeper.utils.DateUtils
import by.iapsit.notikeeper.view.enums.ScreenState

@BindingAdapter("displayed_child")
fun setViewFlipperDisplayedChild(viewFlipper: ViewFlipper, screenState: ScreenState?) {
    viewFlipper.displayedChild = screenState?.viewIndex ?: ScreenState.ERROR.viewIndex
}

@BindingAdapter("app:format_date")
fun setFormatDate(view: TextView, time: Long) {
    view.text = DateUtils.formatDate(time)
}