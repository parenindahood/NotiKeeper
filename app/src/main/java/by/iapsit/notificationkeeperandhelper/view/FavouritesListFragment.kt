package by.iapsit.notificationkeeperandhelper.view

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
import by.iapsit.notificationkeeperandhelper.databinding.FragmentFavouritesListBinding
import by.iapsit.notificationkeeperandhelper.utils.SwipeTouchHelper
import by.iapsit.notificationkeeperandhelper.viewModel.FavouritesListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouritesListFragment : Fragment() {

    private val favouritesAdapter by inject<ApplicationListAdapter> {
        val openNotificationListAction: (String) -> Unit = {
            findNavController().navigate(
                FavouritesListFragmentDirections.actionFavouritesListToNotificationList(
                    it
                )
            )
        }
        val setApplicationFavouriteAction: (String) -> Unit = {
            viewModel.deleteFavouriteApplication(it)
        }
        parametersOf(openNotificationListAction, setApplicationFavouriteAction)
    }

    private val itemTouchHelperCallback by lazy {
        object : SwipeTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val list = favouritesAdapter.currentList.toMutableList()
                val application = list[viewHolder.adapterPosition]
                list.remove(application)
                viewModel.deleteNotificationsByPackageName(application.packageName)
                viewModel.deleteFavouriteApplication(application.packageName)
                favouritesAdapter.submitList(list)
            }
        }
    }

    private lateinit var binding: FragmentFavouritesListBinding

    private val viewModel by viewModel<FavouritesListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favourites_list, container, false)

        setListAdapter()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.applicationListLiveData.observe(requireActivity(), {
            favouritesAdapter.submitList(it)
        })

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    private fun setListAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favouritesAdapter
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                ).apply { setDrawable(resources.getDrawable(R.drawable.drawable_divider)) }
            )
        }
    }
}