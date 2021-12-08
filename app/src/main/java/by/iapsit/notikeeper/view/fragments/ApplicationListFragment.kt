package by.iapsit.notikeeper.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
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
import by.iapsit.notikeeper.databinding.FragmentApplicationListBinding
import by.iapsit.notikeeper.db.entities.FavouriteApplicationEntity
import by.iapsit.notikeeper.utils.Constants
import by.iapsit.notikeeper.utils.SwipeTouchHelper
import by.iapsit.notikeeper.utils.makeSnackBarWithAction
import by.iapsit.notikeeper.utils.makeVibration
import by.iapsit.notikeeper.view.FlowActivity
import by.iapsit.notikeeper.viewModel.ApplicationListViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ApplicationListFragment : Fragment() {

    private lateinit var binding: FragmentApplicationListBinding

    private val viewModel by viewModel<ApplicationListViewModel>()

    private lateinit var preferences: SharedPreferences

    private val vibrator by inject<Vibrator> { parametersOf(requireContext()) }

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
            vibrator.makeVibration(Constants.VIBRATION_DURATION)
        }
        parametersOf(
            openNotificationListAction, setApplicationFavouriteAction, false
        )
    }

    private val itemTouchHelperCallback by lazy {
        object : SwipeTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val packageName =
                    applicationAdapter.currentList[viewHolder.adapterPosition].packageName

                viewModel.deleteNotificationsByPackageName(packageName)

                vibrator.makeVibration(Constants.VIBRATION_DURATION)

                with(resources) {
                    requireActivity().makeSnackBarWithAction(
                        getString(R.string.item_deleted),
                        getString(R.string.cancel),
                        binding.root
                    ) {
                        viewModel.undoDeleteNotifications(packageName)
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
                ).apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.drawable_application_divider,
                            null
                        )!!
                    )
                }
            )
        }
    }
}