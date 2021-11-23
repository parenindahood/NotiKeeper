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
import by.iapsit.notikeeper.databinding.ItemFilterApplicationBinding
import by.iapsit.notikeeper.model.ApplicationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ApplicationFilterAdapter(
    private val insertFilteredAction: (String) -> Unit,
    private val deleteFilteredAction: (String) -> Unit,
    private val checkIsFilteredAction: suspend (String) -> Boolean
) : ListAdapter<ApplicationData, ApplicationFilterAdapter.ViewHolder>(DiffCallback), Filterable {

    private var list = listOf<ApplicationData>()

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

    class ViewHolder(val binding: ItemFilterApplicationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemFilterApplicationBinding>(
                inflater,
                R.layout.item_filter_application,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentApplication = getItem(position)
        with(holder.binding) {
            application = currentApplication
            CoroutineScope(Dispatchers.Main).launch {
                val task = CoroutineScope(Dispatchers.IO).async {
                    checkIsFilteredAction.invoke(currentApplication.packageName)
                }
                filterSwitch.isChecked = task.await()
            }
            filterSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) insertFilteredAction.invoke(currentApplication.packageName) else deleteFilteredAction.invoke(
                    currentApplication.packageName
                )
            }
        }
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString().lowercase()
            val filteredList = if (charSearch.isBlank()) {
                list
            } else {
                val resultList = mutableListOf<ApplicationData>()
                for (row in list) {
                    if (row.appName.lowercase().contains(charSearch)) resultList.add(row)
                }
                resultList
            }
            return FilterResults().apply {
                values = filteredList
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            submitList(results?.values as MutableList<ApplicationData>)
        }

    }

    fun setData(list: List<ApplicationData>) {
        this.list = list
        submitList(list)
    }
}