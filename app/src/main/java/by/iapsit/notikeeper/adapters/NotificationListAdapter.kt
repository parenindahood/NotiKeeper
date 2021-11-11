package by.iapsit.notikeeper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.databinding.ItemNotificationBinding
import by.iapsit.notikeeper.model.NotificationData

class NotificationListAdapter :
    ListAdapter<NotificationData, NotificationListAdapter.ViewHolder>(DiffCallback), Filterable {

    private var list = listOf<NotificationData>()

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

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            val filteredList = if (charSearch.isBlank()) {
                list
            } else {
                val resultList = mutableListOf<NotificationData>()
                for (row in list) {
                    if (row.title.lowercase().contains(charSearch) || row.text.lowercase()
                            .contains(charSearch)
                    ) resultList.add(row)
                }
                resultList
            }
            return FilterResults().apply {
                values = filteredList
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            submitList(results?.values as MutableList<NotificationData>)
        }
    }

    fun setData(list: List<NotificationData>) {
        this.list = list
        submitList(list)
    }
}