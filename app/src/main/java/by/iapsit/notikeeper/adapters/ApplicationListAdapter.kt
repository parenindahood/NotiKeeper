package by.iapsit.notikeeper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.databinding.ItemApplicationBinding
import by.iapsit.notikeeper.model.ApplicationData

class ApplicationListAdapter(
    private val openNotificationListAction: (String) -> Unit,
    private val setApplicationFavouriteAction: (String) -> Unit,
    private val isFavourite: Boolean
) : ListAdapter<ApplicationData, ApplicationListAdapter.ViewHolder>(DiffCallback) {

    private companion object DiffCallback : DiffUtil.ItemCallback<ApplicationData>() {
        override fun areItemsTheSame(
            oldItem: ApplicationData,
            newItem: ApplicationData
        ): Boolean {
            return oldItem.appName == newItem.appName
        }

        override fun areContentsTheSame(
            oldItem: ApplicationData,
            newItem: ApplicationData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    class ViewHolder(val binding: ItemApplicationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemApplicationBinding>(
                inflater,
                R.layout.item_application,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val currentApplication = getItem(position)
            application = currentApplication
            root.setOnClickListener {
                openNotificationListAction.invoke(currentApplication.packageName)
            }
            favouriteButton.setOnClickListener {
                setApplicationFavouriteAction.invoke(currentApplication.packageName)
            }
            if (isFavourite) favouriteButton.setColorFilter(holder.itemView.context!!.getColor(R.color.purple_500))
        }
    }
}