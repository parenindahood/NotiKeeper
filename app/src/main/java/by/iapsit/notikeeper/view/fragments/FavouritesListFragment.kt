package by.iapsit.notikeeper.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.adapters.ApplicationListAdapter
import by.iapsit.notikeeper.databinding.FragmentFavouritesListBinding
import by.iapsit.notikeeper.db.entities.FavouriteApplicationEntity
import by.iapsit.notikeeper.utils.SwipeTouchHelper
import by.iapsit.notikeeper.utils.makeSnackBarWithAction
import by.iapsit.notikeeper.viewModel.FavouritesListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouritesListFragment : Fragment() {

    private val uiScope = CoroutineScope(Dispatchers.Main)

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
        parametersOf(openNotificationListAction, setApplicationFavouriteAction, true)
    }

    private val itemTouchHelperCallback by lazy {
        object : SwipeTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                uiScope.launch {
                    val packageName =
                        favouritesAdapter.currentList[viewHolder.adapterPosition].packageName
                    val deletedList = viewModel.getNotificationsByPackageName(packageName)

                    viewModel.deleteNotificationsByPackageName(packageName)
                    viewModel.deleteFavouriteApplication(packageName)

                    with(resources) {
                        requireActivity().makeSnackBarWithAction(
                            getString(R.string.item_deleted),
                            getString(R.string.cancel),
                            binding.root
                        ) {
                            viewModel.insertFavouriteApplication(
                                FavouriteApplicationEntity(
                                    packageName
                                )
                            )
                            viewModel.undoDeleteApplication(deletedList)
                        }
                    }
                }
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

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}