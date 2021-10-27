package by.iapsit.notificationkeeperandhelper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.databinding.ItemNotificationBinding
import by.iapsit.notificationkeeperandhelper.model.NotificationData

class NotificationListAdapter : ListAdapter<NotificationData, NotificationListAdapter.ViewHolder>(DiffCallback) {

    private companion object DiffCallback : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemNotificationBinding>(
                inflater,
                R.layout.item_notification,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            notification = getItem(position)
        }
    }
}