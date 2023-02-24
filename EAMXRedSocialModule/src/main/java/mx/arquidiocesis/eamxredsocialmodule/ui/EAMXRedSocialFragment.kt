package mx.arquidiocesis.eamxredsocialmodule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.eamx_red_social_fragment.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.urlValidator
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.EAMXPublicationsAllAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxRedSocialFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllRequest
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.news.detail.EAMXDetailFragment
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

const val EDITAR = "EDITAR"
const val ELIMINAR = "ELIMINAR"
const val COMENTARIO = "COMENTARIO"
const val COMPARTIR = "COMPARTIR"
const val LIKE = "LIKE"
const val LIKED = "LIKED"
const val PERFIL = "PERFIL"
const val SEGUIR = "SEGUIR"
const val IDPOST = "IDPOST"
const val SEARCH = "SEARCH"
const val FOLLOW = "FOLLOW"
const val UNFOLLOW = "UNFOLLOW"

class EAMXRedSocialFragment(val isPrincipal: Boolean, var id_user: Int) : FragmentBase() {

    lateinit var binding: EamxRedSocialFragmentBinding

    lateinit var viewmodel: RedSocialViewModel
    lateinit var adapter: EAMXPublicationsAllAdapter
    lateinit var mBottomSheetFragment: EAMXPostFragment
    var model = MutableLiveData<PostModel?>()
    var isLoadingMore = false
    val request = EAMXPublicationsAllRequest()
    var totalLastResult = -1
    var cargado = false
    var istFirst = true
    var maximo = 0
    var list = listOf<ResultMultiProfileModel>()

    lateinit var resultModel: ResultModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        maximo = if(isPrincipal) maximo else 21
        maximo.toString().log()
        viewmodel = RedSocialViewModel(Repository(requireContext()))
        initObservers()
        binding = EamxRedSocialFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_RedSocial")
            })
        }
        initView()
    }

    fun initElements() {
        binding.apply {
            //txtName.text = nameCompleted
            tvNew.setOnClickListener {
                model.value = null
                showBottonSheeat()

            }
            val idUser = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID_REDSOCIAL.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
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
            val Image = eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            id_user = if(isPrincipal) idUser else id_user
            val nameCompleted = "$name $lastName $middleName"
            tvMiRed.setOnClickListener {
                changeFragment(
                    EAMXFollowFragment(idUser,nameCompleted,Image)
                )
            }
            ivUserImage.setOnClickListener {
                changeFragment(
                    EAMXFollowFragment(idUser,nameCompleted,Image)
                )
            }
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
                search()
            }
            ImagenProfile().loadImageProfile(ivUser, requireContext())
            swrRefresh.setColorSchemeResources(R.color.primaryColor)
            swrRefresh.setOnRefreshListener {
                getAllPost()
            }
        }


    }

    fun initObservers() {

        viewmodel.responseAllPost.observe(viewLifecycleOwner) { item ->
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
                        //adapter.items.addAll(resultModel.posts)
                        adapter.items.addAll(
                            if (isPrincipal) {
                                resultModel.posts
                            } else {
                                resultModel.posts.filter { it.author.id == id_user }
                            }
                        )
                        adapter.notifyDataSetChanged()
                    }
                    resultModel.pagination?.let { p ->
                        if (p.hasMore && maximo > 0) {
                            maximo--
                            viewmodel.requestAllpostMi(id_user,p.next)
                        } else {
                            //showSkeleton(false)
                            cargado = true
                            binding.swrRefresh.isRefreshing = false
                        }
                    }

                }

            }
            val prevSize = adapter.items.size
            if (prevSize != 0) {
                adapter.notifyItemRangeInserted(prevSize, adapter.items.count() - 1)
            } else {

            }
        }
        viewmodel.responseDelete.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            getAllPost()
        }
        viewmodel.responseReact.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            getAllPost()
        }
        viewmodel.responseProfile.observe(viewLifecycleOwner) {
            eamxcu_preferences.saveData(EAMXEnumUser.USER_ID_REDSOCIAL.name, it.result.id)
            viewmodel.setProfileId()
            viewmodel.getMultiProfile()
        }
        viewmodel.responseMultiProfile.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            it.result?.let {
                list = it
            }
            getAllPost()
            showSkeleton(false)
        }
        viewmodel.errorBottom.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
        viewmodel.error.observe(viewLifecycleOwner) {
            //showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    fun initView() {
        if (!isPrincipal) {
            binding.apply {
                tvNew.visibility = View.GONE
                tabs.visibility = View.GONE
                iv_user_image.visibility = View.GONE
                cardView.visibility = View.GONE
            }
        }
        if (istFirst) {
            istFirst = false
            showSkeleton(true)
            viewmodel.getProfile()
        } else {
            getAllPost()
        }
        initElements()

    }

    fun selectRow(item: PostModel) {
        val bundle = Bundle()
        bundle.putParcelable(EAMXEnumUser.PUBLICATIONS.name, item)
        if (item.content.urlValidator()) {
            val uri = Uri.parse(item.content)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        } else {
            changeFragment(bundle, EAMXDetailFragment())
        }
    }

    fun getAllPost() {
        adapter = EAMXPublicationsAllAdapter(requireContext(), list, viewmodel.getSuper(),isPrincipal)
        adapter.items = arrayListOf<PostModel>()
        setupRecyclerView()
        click()
        //maximo = 0
        //showSkeleton(true)
        viewmodel.requestAllpostMi(id_user)
    }

    fun dismmisPost() {
        mBottomSheetFragment.dismiss()
        getAllPost()
    }

    fun showBottonSheeat() {
        mBottomSheetFragment = EAMXPostFragment(model, list) {
            if (it)
                getAllPost()
        }
        mBottomSheetFragment.show(
            requireActivity().supportFragmentManager,
            "EAMXPostFragment"
        )
    }

    fun rechargePost() {
        cargado = false
        if (resultModel.pagination!!.hasMore) {
            //maximo = 0
            //showSkeleton(true)
            viewmodel.requestAllpostMi(id_user,resultModel.pagination!!.next)
            binding.swrRefresh.isRefreshing = true
        } else {
            binding.swrRefresh.isRefreshing = false
        }

    }

    fun click() {
        adapter.onItemClickListener = { item, Etiqueta ->
            Log.d("red_social",Etiqueta)
            when (Etiqueta) {
                EDITAR -> {
                    model.value = item
                    showBottonSheeat()
                }
                ELIMINAR -> {
                    UtilAlert.Builder()
                        .setTitle("¿Seguro que quieres eliminar esta publicación?")
                        .setTextButtonOk("Aceptar")
                        .setTextButtonCancel("Cancelar")
                        .setListener { action ->
                            when (action) {
                                UtilAlert.ACTION_ACCEPT -> {
                                    //showSkeleton(true)
                                    viewmodel.deletePost(item.id)
                                }
                                UtilAlert.ACTION_CANCEL -> {

                                }
                            }
                        }
                        .build()
                        .show(childFragmentManager, tag)
                }
                COMENTARIO -> {
                    changeFragment(
                        EAMXComentFragment(item,list)
                    )

                }
                COMPARTIR -> {

                }
                LIKE -> {
                    //showSkeleton(true)
                    viewmodel.reactPost(item.id)
                }
                LIKED -> {
                    item.reaction?.let {
                        showSkeleton(true)
                        viewmodel.reactDel(it.id)
                    }

                }
                SEGUIR -> {

                }
                PERFIL -> {
                    changeFragment(EAMXFollowFragment(item.author.id, item.author.name, item.author.image))
                }
                "" -> {
                    selectRow(item)
                }
            }
        }
    }

    private fun showSkeleton(show: Boolean) {
        binding.apply {
            swrRefresh.visibility = if (show) View.GONE else View.VISIBLE
            llSkeleton.apply {
                llSkeleton.visibility = if (show) View.VISIBLE else View.GONE
                if (show) {
                    shimmerFaithful.startShimmer()
                } else {
                    shimmerFaithful.stopShimmer()
                }
            }
        }
    }

    fun search() {
        if (binding.svBusarRed.query.isNotEmpty()) {
            var bundle = Bundle()
            bundle.putString(SEARCH, binding.svBusarRed.query.toString())
            changeFragment(
                bundle,
                EAMXSearchFragment()
            )

        }
    }

    fun changeFragment(bundle: Bundle, fragment: Fragment) {
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setFragment(fragment)
            .setBundle(bundle)
            .build().nextWithReplace()

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