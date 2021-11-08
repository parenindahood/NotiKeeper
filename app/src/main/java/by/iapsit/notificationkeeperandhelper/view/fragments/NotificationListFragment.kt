package by.iapsit.notificationkeeperandhelper.view.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.adapters.NotificationListAdapter
import by.iapsit.notificationkeeperandhelper.databinding.FragmentNotificationListBinding
import by.iapsit.notificationkeeperandhelper.utils.SwipeTouchHelper
import by.iapsit.notificationkeeperandhelper.utils.makeSnackBarWithAction
import by.iapsit.notificationkeeperandhelper.view.FlowActivity
import by.iapsit.notificationkeeperandhelper.viewModel.NotificationListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding

    private val viewModel by viewModel<NotificationListViewModel> {
        parametersOf(
            NotificationListFragmentArgs.fromBundle(
                requireArguments()
            ).packageName
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
                notificationAdapter.setData(list)

                with(resources) {
                    requireActivity().makeSnackBarWithAction(
                        getString(R.string.item_deleted),
                        getString(R.string.cancel),
                        binding.root
                    ) {
                        viewModel.undoDeleteNotification(notification)
                    }
                }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.notification_list_menu, menu)
        val searchView = SearchView((requireContext() as FlowActivity).supportActionBar?.themedContext ?: requireContext())
        menu.findItem(R.id.action_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.removeObserverLiveData()
                notificationAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) viewModel.observeLiveData()
                return false
            }
        })
    }

    private fun observeLiveData() {
        with(viewModel) {
            notificationListLiveData.observe(requireActivity(), {
                notificationAdapter.setData(it)
            })
            applicationInfoLiveData.observe(requireActivity(), {
                binding.application = it
            })
        }
    }

    private fun setListAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = notificationAdapter
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                ).apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.drawable_divider,
                            null
                        )!!
                    )
                }
            )
        }
    }
}