package by.iapsit.notificationkeeperandhelper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.adapters.NotificationListAdapter
import by.iapsit.notificationkeeperandhelper.databinding.FragmentNotificationListBinding
import by.iapsit.notificationkeeperandhelper.utils.SwipeTouchHelper
import by.iapsit.notificationkeeperandhelper.viewModel.NotificationListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding

    private val viewModel by viewModel<NotificationListViewModel> {
        parametersOf(
            NotificationListFragmentArgs.fromBundle(requireArguments()).packageName
        )
    }

    private val notificationAdapter by inject<NotificationListAdapter>()

    private val itemTouchHelperCallback by lazy {
        object : SwipeTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val list = notificationAdapter.currentList.toMutableList()
                val notification = list[viewHolder.adapterPosition]
                list.remove(notification)
                viewModel.deleteNotificationByID(notification.id)
                notificationAdapter.submitList(list)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationListBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setListAdapter()

        observeLiveData()

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    private fun observeLiveData() {
        with(viewModel) {
            notificationListLiveData.observe(requireActivity(), {
                notificationAdapter.submitList(it)
            })
            applicationInfoLiveData.observe(requireActivity(), {
                binding.application = it
            })
        }
    }

    private fun setListAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                ).apply { setDrawable(resources.getDrawable(R.drawable.drawable_divider)) }
            )
        }
    }
}