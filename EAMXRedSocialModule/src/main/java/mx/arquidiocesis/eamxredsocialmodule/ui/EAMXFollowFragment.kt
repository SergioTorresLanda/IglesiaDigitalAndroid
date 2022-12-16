package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemFollowerBinding
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.FollowAdapter
import mx.arquidiocesis.eamxredsocialmodule.adapter.ViewPagerRedAdapter
import mx.arquidiocesis.eamxredsocialmodule.model.FollowModel
import mx.arquidiocesis.eamxredsocialmodule.model.SearchModel
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.GetPerfilImagen
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EAMXFollowFragment : FragmentBase() {
    lateinit var binding: ItemFollowerBinding
    lateinit var followAdapter: FollowAdapter
    lateinit var followesAdapter: FollowAdapter
    var new = true
    var listFollow = mutableListOf<FollowModel>()
    var listFollowes = mutableListOf<FollowModel>()
    var type = 1

    val viewModel: RedSocialViewModel by lazy {
        getViewModel {
            RedSocialViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ItemFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name =
            eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
                .toString()
        val lastName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_LAST_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val middleName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_MIDDLE_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val nameCompleted = "$name $lastName $middleName"
        binding.apply {
            GetPerfilImagen().loadImageProfile(
                requireContext(),
                eamxcu_preferences.getData(
                    EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String, ivUser
            )
            tvUserName.text = nameCompleted
        }
        initObservers()
        showLoader()
        viewModel.getFollow(type)
    }

    fun initObservers() {
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
                    if (resultModel.Pagination.hasMore) {
                        viewModel.getFollow(type, resultModel.Pagination.next)
                    } else {
                        followAdapter = FollowAdapter(
                            listFollow
                        ) { //item ->
                            //seguir(item, 1)
                        }
                        type = 2
                        viewModel.getFollow(type)
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
                    if (resultModel.Pagination.hasMore) {
                        viewModel.getFollow(type, resultModel.Pagination.next)
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
            viewModel.getFollow(2)
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
        item.type?.let { t ->
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
        var adapter = ViewPagerRedAdapter(this, followAdapter, followesAdapter)
        binding.vpFollower.adapter = adapter
        TabLayoutMediator(binding.tlFollower, binding.vpFollower) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "${listFollowes.size} Seguidores"
                    // tab.icon = drawable1
                }
                1 -> {
                    tab.text = "${listFollow.size} Seguidos"
                    // tab.icon = drawable1
                }
            }

        }.attach()


    }
}