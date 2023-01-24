package mx.upax.formacion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.upax.formacion.R
import com.upax.formacion.databinding.FragmentFormacionNewBinding
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.openYoutubeApi
import mx.arquidiocesis.eamxcommonutils.util.visibility
import mx.upax.formacion.adapter.MenuAdapter
import mx.upax.formacion.adapter.OthersNewAdapter
import mx.upax.formacion.model.BaseModel
import mx.upax.formacion.model.FeaturedModel
import mx.upax.formacion.repository.Repository
import mx.upax.formacion.viewModel.FormationViewModel
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager

const val PDF = "FILE"
const val YOUTUBE = "VIDEO"
const val WEB = "LINK"
const val OUTSTANDING = "OUTSTANDING"

class FormationFragment: FragmentBase() {

    private val TAG = this.javaClass.name

    companion object {
        fun newInstance(callBack: EAMXHome) =
            FormationFragment().apply { this.callBack = callBack }
    }

    private lateinit var binding: FragmentFormacionNewBinding
    private val viewModel : FormationViewModel by lazy {
        getViewModel {
            FormationViewModel(Repository(context = requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormacionNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view_tag", Bundle().apply {
                putString("screen_name", "Android_BibliotecaVirtual")
            })
        }
        initView()
        initObservers()
    }

    private fun initView() {
        callBack.showToolbar(true, AppMyConstants.formacion)
        binding.apply {
            svSearchData.queryHint = getString(R.string.hint_search_biblio)
            svSearchData.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    showEmptyState(
                        (binding.rvContentInfo.adapter as? OthersNewAdapter)?.setTextFilter(
                            newText
                        ) != true, newText
                    )
                    return true
                }
            })
            bvTabs.setOnItemSelectedListener {
                filterAdapterCategory(it.itemId)
                return@setOnItemSelectedListener true
            }
        }
        getMenuPrincipal()
    }

    private fun filterAdapterCategory(itemId: Int?) {
        showEmptyState(
            (binding.rvContentInfo.adapter as? OthersNewAdapter)
                ?.setCategory((itemId ?: binding.bvTabs.selectedItemId).getCategorySelected) != true
        )
    }

    private fun getMenuPrincipal() {
        showLoader(TAG)
        viewModel.loadViewMenu()
    }

    private fun initObservers() {
        viewModel.searchTabNewsResponse.observe(viewLifecycleOwner) { data ->
            hideLoader()
            hiddenSearchView()
        }

        viewModel.searchInfoContent.observe(viewLifecycleOwner) { data ->
            setAdaptersTabOthers(data)
            filterAdapterCategory(null)
            hideLoader()
            hiddenSearchView()
        }

        viewModel.tabNavigationResponse.observe(viewLifecycleOwner) { response ->
            val adapterMenu = MenuAdapter(response.data.toMutableList()) { itemTab ->
                showLoader(TAG)
                binding.svSearchData.setQuery("",true)
                binding.bvTabs.menu.clear()
                binding.bvTabs.inflateMenu(if (itemTab?.code == OUTSTANDING) R.menu.menu_tab_outstanding else R.menu.menu_tab_others)
                viewModel.searchInformation(tabCategory = itemTab?.code ?: OUTSTANDING)
            }
            adapterMenu.setDefualtFirstTabSelected()
            binding.rvNavigationBarCustom.adapter = adapterMenu
            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            hiddenSearchView()
            showEmptyState(true)

            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .setIsCancel(false)
                .build()
                .show(childFragmentManager, TAG)
        }
    }

    private fun hiddenSearchView() = binding.svSearchData.clearFocus()

    private fun setAdaptersTabOthers(featuredList: List<FeaturedModel>) {
        binding.rvContentInfo.adapter = OthersNewAdapter(featuredList) { item ->
            if (item.type == YOUTUBE) executeIntent(item.url, item.id)
            else executeIntentWeb(item)
        }
    }

    private fun showEmptyState(show: Boolean, textEmpty: String? = null) {
        binding.clEmpty.visibility(show)
        binding.rvContentInfo.visibility(!show)
        binding.tvEmptyText.text =
            if (!textEmpty.isNullOrEmpty()) getString(R.string.message_default_empty, textEmpty)
            else getString(R.string.message_default_empty_without_category)
    }

    private fun executeIntent(url: String, itemIdentifier: Int) {
        viewModel.updateViewInItem(itemIdentifier)
        openYoutubeApi(url)
    }

    private val Int.getCategorySelected
        get() = when (this) {
            R.id.FILE -> "FILE"
            R.id.VIDEO -> "VIDEO"
            R.id.AUDIO -> "AUDIO"
            R.id.TEXTO -> "TEXT"
            R.id.LINK -> "LINK"
            else -> null

        }

    private fun executeIntentWeb(item: BaseModel) {
        viewModel.updateViewInItem(item.id)
        val fragment = OpenDataFragment.newInstance(callBack)
        fragment.apply {
            arguments = Bundle().apply {
                putString(item.type, item.url)
            }
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.add((requireView().parent as ViewGroup).id, fragment)
        transaction.addToBackStack(TAG)
        transaction.commit()
    }
}