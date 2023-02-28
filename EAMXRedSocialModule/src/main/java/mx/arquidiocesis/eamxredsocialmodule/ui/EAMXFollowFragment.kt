package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.item_follower.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemFollowerBinding
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.FollowAdapter
import mx.arquidiocesis.eamxredsocialmodule.adapter.PostAdapter
import mx.arquidiocesis.eamxredsocialmodule.adapter.ViewPagerRedAdapter
import mx.arquidiocesis.eamxredsocialmodule.model.FollowModel
import mx.arquidiocesis.eamxredsocialmodule.model.MetadataModel
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultModel
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EAMXFollowFragment(
    val idUser: Int, val Name: String, val Image: String?, val siguiendo: Boolean = true,
    val metadata: MetadataModel? = MetadataModel(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ),
) : FragmentBase() {
    lateinit var binding: ItemFollowerBinding

    //lateinit followAdapter: FollowAdapter
    lateinit var followAdapter: FollowAdapter
    lateinit var followesAdapter: FollowAdapter
    lateinit var postsAdapter: PostAdapter
    lateinit var resultModel: ResultModel
    var new = true
    var listPost = mutableListOf<PostModel>()
    var listFollow = mutableListOf<FollowModel>()
    var listFollowes = mutableListOf<FollowModel>()
    var type = 1

    //Provicional
    var cargado = false
    var maximo = 21
    var maximo_follow = 29
    var maximo_followes = 29

    val viewModel: RedSocialViewModel by lazy {
        getViewModel {
            RedSocialViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ItemFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoader()
        viewModel.requestAllpostMi(idUser)
        initObservers()
        binding.apply {
            val profileId = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID_REDSOCIAL.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
            if (idUser != profileId) {
                tabs.visibility = View.GONE
                ivUserImage.setMargins(top = 32)
                if (siguiendo) {
                    ivSeguiendo.visibility = View.VISIBLE
                    ivSegir.visibility = View.GONE
                    ivSeguiendo.setOnClickListener {
                        metadata?.let { m ->
                            m.personId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 1, true)
                            } ?: m.communityId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 3, true)
                            } ?: m.churchId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 2, true)
                            }
                        }
                        ivSeguiendo.visibility = View.GONE
                        ivSegir.visibility = View.VISIBLE
                    }
                } else {
                    ivSeguiendo.visibility = View.GONE
                    ivSegir.visibility = View.VISIBLE
                    ivSegir.setOnClickListener {
                        metadata?.let { m ->
                            m.personId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 1, false)
                            } ?: m.communityId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 3, false)
                            } ?: m.churchId?.let {
                                showLoader()
                                viewModel.followPost(idUser, 2, false)
                            }
                        }
                        ivSeguiendo.visibility = View.VISIBLE
                        ivSegir.visibility = View.GONE
                    }
                }
            } else {
                tv_user_name.setMargins(right = 0)
                ivSeguiendo.visibility = View.GONE
                tvPublicaciones.setOnClickListener { activity?.onBackPressed() }
            }
            ivUser.loadByUrlIntDrawableerror(Image ?: "", R.drawable.user)
            tvUserName.text = Name
        }
    }

    fun initObservers() {
        //viewModel.getFollow(type, null, idUser)
        viewModel.responseAllPost.observe(viewLifecycleOwner) { item ->
            if (new) {
                new = false
                listPost = mutableListOf<PostModel>()
            }
            item?.let { i ->
                i.result?.let { r ->
                    resultModel = r
                    if (!resultModel.posts.isNullOrEmpty()) {
                        resultModel.posts.forEach {
                            it.multimedia.forEach {
                                if (it.format == "jpeg" || it.format == "jpg")
                                    it.format = "image"
                            }
                        }
                        listPost.addAll(resultModel.posts.filter { it.author.id == idUser })
                        //listPost.addAll(resultModel.posts)
                        //resultModel.posts
                        //postsAdapter.notifyDataSetChanged()
                    }
                    resultModel.pagination?.let { p ->
                        if (p.hasMore && maximo > 0) {
                            maximo--
                            viewModel.requestAllpostMi(idUser, p.next)
                        } else {
                            //showSkeleton(false)
                            new = true
                            viewModel.getFollow(type, null, idUser)
                            cargado = true
                            //binding.swrRefresh.isRefreshing = false
                        }
                    }
                }
            }
            val prevSize = listPost.size
            if (prevSize != 0) {
                postsAdapter = PostAdapter(listPost) { item ->
                    Log.e("Post", "Si")
                }
                //postsAdapter.notifyItemRangeInserted(prevSize, postsAdapter.items.count() - 1)
            } else {
            }
            //postsAdapter.list.size.toString().log()
        }
        viewModel.reponseFollow.observe(viewLifecycleOwner) {
            if (new) {
                new = false
                listFollow = mutableListOf<FollowModel>()
            }
            it!!.result?.let { resultModel ->
                if (!resultModel.Follows.isNullOrEmpty()) {
                    resultModel.Follows.forEach {
                        listFollow.add(it)
                    }
                }
                resultModel.Pagination?.let {
                    if (resultModel.Pagination.hasMore && maximo_follow > 0) {
                        maximo_follow--
                        viewModel.getFollow(type, resultModel.Pagination.next, idUser)
                    } else {
                        followAdapter = FollowAdapter(
                            listFollow
                        ) { item ->
                            seguir(item, 1)
                        }
                        type = 2
                        new = true
                        viewModel.getFollow(type, null, idUser)
                    }
                }
            }
        }
        viewModel.reponseFollowes.observe(viewLifecycleOwner) {
            if (new) {
                new = false
                listFollowes = mutableListOf<FollowModel>()
            }
            it!!.result?.let { resultModel ->
                if (!resultModel.Follows.isNullOrEmpty()) {
                    resultModel.Follows.forEach {
                        listFollowes.add(it)
                    }
                }
                resultModel.Pagination?.let {
                    if (resultModel.Pagination.hasMore && maximo_followes > 0) {
                        maximo_followes--
                        viewModel.getFollow(type, resultModel.Pagination.next, idUser)
                    } else {
                        followesAdapter = FollowAdapter(
                            listFollowes
                        ) { item ->
                            seguir(item, 2)
                        }
                        initView()
                    }
                }
            }
        }
        viewModel.reponseFollowPost.observe(viewLifecycleOwner) {
            new = true
            viewModel.getFollow(2, null, idUser)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(it)
                .build()
                .show(childFragmentManager, tag)
        }

    }

    fun seguir(item: FollowModel, tipo: Int) {
        type = tipo
        var follower = false
        item.relationshipStatus?.let { r ->
            if (r == 1 || r == 3) {
                follower = true
            }
        }
        item.type.let { t ->
            when (t) {
                "User" -> {
                    showLoader()
                    viewModel.followPost(item.id, 1, follower)
                }
                "Community" -> {
                    showLoader()
                    viewModel.followPost(item.id, 3, follower)
                }
                "Church" -> {
                    showLoader()
                    viewModel.followPost(item.id, 2, follower)
                }
            }

        }
    }

    fun initView() {
        hideLoader()
        binding.vpFollower.adapter =
            ViewPagerRedAdapter(this, null, followesAdapter, followAdapter, idUser)
        TabLayoutMediator(binding.tlFollower, binding.vpFollower) { tab, position ->
            when (position) {
                0 -> {
                    tab.setCustomView(R.layout.item_perfile)
                    tab.customView?.findViewById<TextView>(R.id.tvConteo)?.text =
                        (if(listPost.size>100) 100 else listPost.size).toString()
                    tab.customView?.findViewById<TextView>(R.id.tvCategoria)?.text = "Publicaciones"
                }
                1 -> {
                    tab.setCustomView(R.layout.item_perfile)
                    tab.customView?.findViewById<TextView>(R.id.tvConteo)?.text =
                        listFollowes.size.toString()
                    tab.customView?.findViewById<TextView>(R.id.tvCategoria)?.text = "Seguidores"
                }
                2 -> {
                    tab.setCustomView(R.layout.item_perfile)
                    tab.customView?.findViewById<TextView>(R.id.tvConteo)?.text =
                        listFollow.size.toString()
                    tab.customView?.findViewById<TextView>(R.id.tvCategoria)?.text = "Seguidos"
                }
            }
        }.attach()
    }
}