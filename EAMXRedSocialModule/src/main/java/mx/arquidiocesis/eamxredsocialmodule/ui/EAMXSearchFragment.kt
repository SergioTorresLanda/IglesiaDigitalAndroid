package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.SearchAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxSearchFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.SearchModel
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EAMXSearchFragment : FragmentBase() {

    lateinit var binding: EamxSearchFragmentBinding
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter { etiqueta, item ->
            var follower = false
            when (etiqueta) {
                FOLLOW -> {
                    follower = false
                }
                UNFOLLOW -> {
                    follower = true
                }
                PERFIL -> {
                    var siguiendo = false
                    item.relationship?.let {
                        if (it.statusId == 1 || it.statusId == 3) {
                            siguiendo = true
                        }
                    }
                    item.name?.let { changeFragment(EAMXFollowFragment(item.id, it, item.image,siguiendo,item.metadata)) }
                }
            }
            item.metadata?.let { m ->
                m.personId?.let {
                    showLoader()
                    viewmodel.followPost(item.id, 1, follower)
                } ?: m.communityId?.let {
                    showLoader()
                    viewmodel.followPost(item.id, 3, follower)
                } ?: m.churchId?.let {
                    showLoader()
                    viewmodel.followPost(item.id, 2, follower)
                }
            }
        }
    }
    var list = mutableListOf<SearchModel>()
    var search = ""
    var clear = true
    var new = true
    val viewmodel: RedSocialViewModel by lazy {
        getViewModel {
            RedSocialViewModel(Repository(requireContext()))
        }
    }
    private var page = "0"
    private var count = 1
    private var hasMore = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = EamxSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initView()
        requireArguments().getString(SEARCH)?.let {
            search = it
            binding.svBusarRed.setQuery(search, false)
            showLoader()
            viewmodel.getSearch(search)
        }
    }

    fun initObservers() {
        viewmodel.reponseSearch.observe(viewLifecycleOwner) {
            list = mutableListOf<SearchModel>()
            if (new) {
                new = false
            }
            it.result?.let { resultModel ->
                if (!resultModel.results.isNullOrEmpty()) {
                    resultModel.results.forEach {
                        list.add(it)
                    }
                }
                resultModel.pagination?.let {
                    eamxLog("PAGINATION", it.toString())
                    hideLoader()
                    if (list.isEmpty()) {
                        binding.llEmptyResult.visibility = View.VISIBLE
                        binding.rvBusqueda.visibility = View.GONE
                    } else {
                        binding.llEmptyResult.visibility = View.GONE
                        binding.rvBusqueda.visibility = View.VISIBLE
                    }

                    searchAdapter.setNewList(list)
                    hasMore = resultModel.pagination.hasMore

                    if (resultModel.pagination.hasMore && count <= 10) {
                        page = resultModel.pagination.next
                        count++
                        viewmodel.getSearch(search, page)
                    }
                }
            }
        }
        viewmodel.reponseFollowPost.observe(viewLifecycleOwner) {
            new = true
            viewmodel.getSearch(search)
        }
        viewmodel.error.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    fun initView() {
        binding.apply {
            svBusarRed.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    search()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                    }
                    return false
                }
            })
            ibBusacar.setOnClickListener {
                if (clear) {
                    svBusarRed.setQuery("", false)
                    clear = false
                    Glide.with(this@EAMXSearchFragment)
                        .load(R.drawable.ic_icono_buscar)
                        .into(ibBusacar)
                } else {
                    search()
                }
            }

            rvBusqueda.apply {
                layoutManager =
                    LinearLayoutManager(
                        activity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                adapter = searchAdapter
            }
        }

    }

    fun search() {
        if (!binding.svBusarRed.query.isNullOrEmpty()) {
            search = binding.svBusarRed.query.toString()
            clear = true
            new = true
            Glide.with(this@EAMXSearchFragment)
                .load(R.drawable.ic_x)
                .into(binding.ibBusacar)
            showLoader()
            viewmodel.getSearch(search)
        }
    }

    fun changeFragment(fragment: Fragment) {
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(fragment)
            .setAllowStack(true)
            .build().nextWithReplace()

    }
}