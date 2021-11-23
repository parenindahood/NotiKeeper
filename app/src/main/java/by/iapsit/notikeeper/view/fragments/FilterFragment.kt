package by.iapsit.notikeeper.view.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iapsit.notikeeper.R
import by.iapsit.notikeeper.adapters.ApplicationFilterAdapter
import by.iapsit.notikeeper.databinding.FragmentFilterBinding
import by.iapsit.notikeeper.view.FlowActivity
import by.iapsit.notikeeper.viewModel.FilterViewModel
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val applicationAdapter by inject<ApplicationFilterAdapter> {
        with(viewModel) {
            val insertFilteredAction: (String) -> Unit = {
                insertFilteredPackageName(it)
            }
            val deleteFilteredAction: (String) -> Unit = {
                deleteFilteredPackageName(it)
            }
            val checkIsFilteredAction: suspend (String) -> Boolean = {
                checkIsFiltered(it)
            }
            return@inject parametersOf(
                insertFilteredAction,
                deleteFilteredAction,
                checkIsFilteredAction
            )
        }
    }

    private val viewModel by viewModel<FilterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setListAdapter()

        observeLiveData()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun observeLiveData() {
        viewModel.applicationListLiveData.observe(requireActivity(), {
            applicationAdapter.setData(it)
        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as FlowActivity).showNotificationsUI()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as FlowActivity).hideNotificationsUI()
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
                            R.drawable.drawable_divider,
                            null
                        )!!
                    )
                }
            )
        }
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
                applicationAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) applicationAdapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}