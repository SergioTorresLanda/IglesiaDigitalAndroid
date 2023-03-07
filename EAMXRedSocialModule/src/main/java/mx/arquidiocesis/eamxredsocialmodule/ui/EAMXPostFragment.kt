package mx.arquidiocesis.eamxredsocialmodule.ui

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.zelory.compressor.Compressor
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.MediaAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxCreateFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import mx.arquidiocesis.eamxredsocialmodule.news.create.update_media.EAMXUpdateMediaFragment
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.FileHelper
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.GetPerfilImagen
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel
import java.io.File

class EAMXPostFragment(
    var model: MutableLiveData<PostModel?>,
    var list: List<ResultMultiProfileModel>? = null,
    val listener: (Boolean) -> Unit
) :
    BottomSheetDialogFragment() {

    lateinit var binding: EamxCreateFragmentBinding
    lateinit var viewmodel: RedSocialViewModel

    private var MEDIA = 100
    private var tipo = 0
    lateinit var adapter: MediaAdapter
    private val loader by lazy { UtilLoader() }
    lateinit var arrayList: ArrayList<String>
    var multimediaList = mutableListOf<EAMXMultimedia>()
    var deleteList = MutableLiveData<MutableList<EAMXMultimedia>>()
    var nameCompleted = ""
    var idUser = 0
    var scope = 1
    var numImag = 0
    var imgUp = 0
    var edit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EamxCreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "RedSocial_CrearPost")
            })
        }
        initView()
        initObservers()
    }

    fun initView() {
        viewmodel = RedSocialViewModel(Repository(requireContext()))
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
        nameCompleted = "$name $lastName $middleName"
        deleteList.value = mutableListOf<EAMXMultimedia>()
        binding.apply {
            setupRecyclerView(rvMedia, !::adapter.isInitialized)
            GetPerfilImagen().loadImageProfile(
                requireContext(),
                eamxcu_preferences.getData(
                    EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String, ivUser
            )
            //editar
            model.value?.let { m ->
                edit = true
                tvTitleNuevo.text = "Editar publicación"
                etComment.setText(m.content)
                m.scope.id?.let { s ->
                    tvUserName.text = m.scope.name
                }?:run{
                    tvUserName.text = nameCompleted
                }
                if (!m.multimedia.isNullOrEmpty()) {
                    m.multimedia.forEach { l ->
                        adapter.items.add(
                            EAMXMultimedia(
                                l.id.toLong(),
                                l.format,
                                l.url,
                                path = l.url
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                    updatAdapter()
                }
            } ?: run{
                if (!list.isNullOrEmpty()) {
                    spNameUser.visibility = View.VISIBLE
                    val lista: ArrayList<String> = ArrayList()
                    lista.add(nameCompleted)
                    list!!.forEach {
                        it.name?.let{n->
                            lista.add(n)
                        }?: run {
                            lista.add(" ")
                        }
                    }
                    val adapterSpinner = ArrayAdapter(
                        requireContext(),
                        R.layout.custom_spinner_red, lista
                    )
                    tvUserName.visibility = View.INVISIBLE
                    spNameUser.adapter = adapterSpinner
                } else {
                    tvUserName.text = nameCompleted
                }
            }
            btnPublicar.setOnClickListener {
                if (!etComment.text.toString().isNullOrEmpty()) {
                    showLoader()
                    getIdProfile()
                    if (adapter.items.isNotEmpty()) {
                        arrayList = arrayListOf()
                        adapter.items.forEach {item->
                            item.id?.let {
                            } ?: arrayList.add("" + item.displayName)
                        }
                        if (arrayList.isNotEmpty()) {
                            viewmodel.setMultimedia(arrayList, idUser)
                        } else {
                            update()
                        }

                    } else {
                        if (edit) {
                            update()
                        } else {
                            post()
                        }
                    }
                } else {
                    UtilAlert
                        .Builder()
                        .setTitle("Aviso")
                        .setMessage("Ingrese un mensaje para compartir")
                        .build()
                        .show(childFragmentManager, "")
                }
            }


            tvAddImagen.setOnClickListener {
                tipo = 1
                permisoMedia()
            }
            tvAddVideo.setOnClickListener {
                tipo = 2
                permisoMedia()
            }
            tvAddArchivo.setOnClickListener {
                tipo = 3
                //permisoMedia()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun initObservers() {
        viewmodel.responseMultimedia.observe(viewLifecycleOwner) { item ->
            var contador = 0
            if(edit){
                adapter.items.forEach {i->
                    i.id?.let {
                    }?:multimediaList.add(i)
                }

            }else{
              multimediaList=adapter.items
            }
            item.forEach {
                multimediaList[contador].url = it.filekey
                multimediaList[contador].preSigned = it.pre_signed
                consumirS3(multimediaList[contador])
                contador++
            }
        }
        viewmodel.responsePutMulti.observe(viewLifecycleOwner) {
            if (it) {
                imgUp++
            }
            numImag++
            if (multimediaList.size == numImag) {
                if (imgUp > 0) {
                    if (edit) {
                        update()
                    } else {
                        post()
                    }
                } else {
                    hideLoader()
                    viewmodel.error.value = "Error al subir imagenes"
                }
            }
        }
        viewmodel.responsePost.observe(viewLifecycleOwner) {
            listener(true)
            dismiss()
        }
        viewmodel.error.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .setListener { dismiss() }
                .build()
                .show(childFragmentManager, "")
        }
        viewmodel.errorBottom.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .setListener { dismiss() }
                .build()
                .show(childFragmentManager, "")
        }
        deleteList.observe(viewLifecycleOwner) {

        }
    }

    private fun post() {
        viewmodel.setPost(
            idUser,
            scope,
            binding.etComment.text.toString(),
            multimediaList
        )
    }

    private fun update() {
        viewmodel.updatePost(
            binding.etComment.text.toString(),
            model.value!!.id,
            deleteList.value as List<EAMXMultimedia>,
            multimediaList
        )
    }

    private fun consumirS3(item: EAMXMultimedia) {
        var file = File(item.path!!)
        if (file.exists()) {
            if (item.isImage()) {
                file = Compressor(context).compressToFile(file)
            }
            viewmodel.putMultimedia(item.preSigned!!, file.readBytes())
        }
    }

    private fun permisoMedia() {
        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                this,
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), MEDIA
            )
        ) {
            getMultimedia()
        }
    }

    private fun getMultimedia() {
        FileHelper.pickMultipleMedia(this, MEDIA, tipo)
    }

    fun setupRecyclerView(recycler: RecyclerView, replaceAdapter: Boolean) {
        recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = if (replaceAdapter) MediaAdapter(
            recyclerView = recycler,
            listener = {
                EAMXUpdateMediaFragment.newInstance(adapter, deleteList)
                    .show(
                        childFragmentManager, EAMXUpdateMediaFragment.TAG
                    )
            }
        ) else adapter
        recycler.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                MEDIA -> {
                    getMultimedia()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val array = adapter.items
        if (resultCode == Activity.RESULT_OK) {
            val uriString = data?.data
            if (data?.clipData == null) {
                val item = ClipData.Item(uriString)
                viewmodel.getFileMetaData(item.uri)?.let { array.add(it) }
            } else {
                val selectedMediaUri = data.clipData
                for (i in 0 until selectedMediaUri!!.itemCount) {
                    val item = selectedMediaUri.getItemAt(i)
                    viewmodel.getFileMetaData(item.uri)?.let { array.add(it) }
                }
            }
            adapter.notifyDataSetChanged()
            updatAdapter()
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

    fun updatAdapter() {
        if (adapter.items.size == 1) {
            binding.itemImage.visibility = View.VISIBLE
            binding.rvMedia.visibility = View.GONE
            Glide
                .with(this)
                .asBitmap()
                .load(adapter.items[0].path)
                .into(binding.itemImage)
            if (!adapter.items[0].isImage())
                binding.ivPlay.visibility = View.VISIBLE
        } else {
            binding.itemImage.visibility = View.GONE
            binding.ivPlay.visibility = View.GONE
            binding.rvMedia.visibility = View.VISIBLE
        }
    }

    fun getIdProfile() {
        binding.apply {
            if (edit) {
                model.value!!.scope.id?.let {
                    idUser = it
                    scope = if(model.value!!.scope.typeId!=null){
                        model.value!!.scope.typeId!!
                    }else{
                        1
                    }
                }
            } else {
                if (spNameUser.visibility == View.VISIBLE) {
                    val select = spNameUser.selectedItemPosition
                    if (select > 0) {
                        val item = list!![select - 1]
                        idUser = item.id
                        scope = item.type.toInt() + 1
                    } else {
                        idUser = 0
                    }
                }
            }

        }
    }

    fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }

}