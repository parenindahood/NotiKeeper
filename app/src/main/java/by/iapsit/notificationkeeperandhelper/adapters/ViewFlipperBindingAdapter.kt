package by.iapsit.notificationkeeperandhelper.adapters

import android.widget.ViewFlipper
import androidx.databinding.BindingAdapter
import by.iapsit.notificationkeeperandhelper.view.ScreenState

@BindingAdapter("displayed_child")
fun setViewFlipperDisplayedChild(viewFlipper: ViewFlipper, screenState: ScreenState?) {
    viewFlipper.displayedChild = screenState?.viewIndex ?: ScreenState.ERROR.viewIndex
}