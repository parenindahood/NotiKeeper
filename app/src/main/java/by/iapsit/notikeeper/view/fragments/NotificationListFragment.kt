package by.iapsit.notikeeper.view.fragments

import android.os.Bundle
import android.os.Vibrator
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.adapters.NotificationListAdapter
import by.iapsit.notikeeper.databinding.FragmentNotificationListBinding
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.utils.SwipeTouchHelper
import by.iapsit.notikeeper.utils.makeSnackBarWithAction
import by.iapsit.notikeeper.utils.makeVibration
import by.iapsit.notikeeper.view.FlowActivity
import by.iapsit.notikeeper.viewModel.NotificationListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding

    private val vibrator by inject<Vibrator> { parametersOf(requireContext()) }

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

                viewModel.deleteNotificationByID(notification.id)

                if (flagSearching) {
                    list.remove(notification)
                    notificationAdapter.setData(list)
                }

                vibrator.makeVibration(Constants.VIBRATION_DURATION)

                with(resources) {
                    requireActivity().makeSnackBarWithAction(
                        getString(R.string.item_deleted),
                        getString(R.string.cancel),
                        binding.root
                    ) {
                        viewModel.undoDeleteNotification(notification.id)
                    }
                }
            }
        }
    }

    private var flagSearching = false

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

    override fun onStart() {
        super.onStart()
        (requireActivity() as FlowActivity).showNotificationsUI()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as FlowActivity).hideNotificationsUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = SearchView(
            (requireContext() as FlowActivity).supportActionBar?.themedContext ?: requireContext()
        )
        menu.findItem(R.id.action_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.removeObserverLiveData()
                notificationAdapter.filter.filter(query)
                flagSearching = true
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) {
                    viewModel.observeLiveData()
                    flagSearching = false
                }
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