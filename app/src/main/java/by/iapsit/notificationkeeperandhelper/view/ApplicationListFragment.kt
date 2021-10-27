package by.iapsit.notificationkeeperandhelper.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notificationkeeperandhelper.R
import by.iapsit.notificationkeeperandhelper.adapters.ApplicationListAdapter
import by.iapsit.notificationkeeperandhelper.databinding.FragmentApplicationListBinding
import by.iapsit.notificationkeeperandhelper.db.entities.FavouriteApplicationEntity
import by.iapsit.notificationkeeperandhelper.utils.SwipeTouchHelper
import by.iapsit.notificationkeeperandhelper.viewModel.ApplicationListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ApplicationListFragment : Fragment() {

    private lateinit var binding: FragmentApplicationListBinding

    private val viewModel by viewModel<ApplicationListViewModel>()

    private lateinit var preferences: SharedPreferences

    private val applicationAdapter by inject<ApplicationListAdapter> {
        val openNotificationListAction: (String) -> Unit = {
            findNavController().navigate(
                ApplicationListFragmentDirections.actionApplicationListToNotificationList(
                    it
                )
            )
        }
        val setApplicationFavouriteAction: (String) -> Unit = {
            viewModel.insertFavouriteApplication(FavouriteApplicationEntity(it))
        }
        parametersOf(
            openNotificationListAction, setApplicationFavouriteAction
        )
    }

    private val itemTouchHelperCallback by lazy {
        object : SwipeTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val list = applicationAdapter.currentList.toMutableList()
                val application = list[viewHolder.adapterPosition]
                list.remove(application)
                viewModel.deleteNotificationsByPackageName(application.packageName)
                applicationAdapter.submitList(list)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_application_list, container, false)

        preferences = (requireActivity() as FlowActivity).preferences

        setListAdapter()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.applicationListLiveData.observe(requireActivity(), {
            applicationAdapter.submitList(it)
        })

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    private fun setListAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applicationAdapter
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                ).apply { setDrawable(resources.getDrawable(R.drawable.drawable_divider)) }
            )
        }
    }
}