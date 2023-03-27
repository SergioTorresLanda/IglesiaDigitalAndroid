package mx.arquidiocesis.eamxredsocialmodule.ui

import com.bumptech.glide.Glide
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.squareup.picasso.Picasso.LoadedFrom
import kotlinx.android.synthetic.main.eamx_red_social_fragment.*
import kotlinx.android.synthetic.main.item_media_preview_social.view.*
import kotlinx.android.synthetic.main.item_red_social.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.urlValidator
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.EAMXPublicationsAllAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxRedSocialFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.*
import mx.arquidiocesis.eamxredsocialmodule.news.detail.EAMXDetailFragment
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel
import java.io.*
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL
import java.time.LocalDateTime


const val EDITAR = "EDITAR"
const val ELIMINAR = "ELIMINAR"
const val COMENTARIO = "COMENTARIO"
const val COMPARTIR = "COMPARTIR"
const val TEXTO = "Texto"
const val IMAGEN = "Imagen"
const val VIDEO = "Video"
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
        savedInstanceState: Bundle?,
    ): View? {
        maximo = if (isPrincipal) maximo else 21
        viewmodel = RedSocialViewModel(Repository(requireContext()))
        binding = EamxRedSocialFragmentBinding.inflate(inflater, container, false)
        initObservers()
        return binding.root
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
                        adapter.items.addAll(
                            if (msgGuest(isMsg = false)) {
                                resultModel.posts.filter { it.author.id != id_user }
                            } else if (isPrincipal) {
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
                            viewmodel.requestAllpostMi(id_user, p.next)
                        } else {
                            cargado = true
                            binding.swrRefresh.isRefreshing = false
                        }
                    }
                }
            }
            val prevSize = adapter.items.size
            if (prevSize != 0) {
                adapter.notifyItemRangeInserted(prevSize, adapter.items.count() - 1)
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
            initElements()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Home_RedSocial")
            })
        }
        initView()
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
            initElements()
            getAllPost()
        }
    }

    fun initElements() {
        binding.apply {
            val idUser = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID_REDSOCIAL.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
            val name = eamxcu_preferences.getData(
                EAMXEnumUser.USER_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
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
            tvNew.setOnClickListener {
                if (!msgGuest("publicar en nuestra red social")) {
                    model.value = null
                    showBottonSheeat()
                }
            }
            id_user = if (isPrincipal) idUser else id_user
            val nameCompleted = "$name $lastName $middleName"
            tvMiRed.setOnClickListener {
                if (!msgGuest("tener un perfil en nuestra red social")) {
                    changeFragment(
                        EAMXFollowFragment(
                            idUser,
                            nameCompleted,
                            Image
                        )
                    )
                }
            }
            ivUserImage.setOnClickListener {
                if (!msgGuest("tener un perfil en nuestra red social")) {
                    changeFragment(
                        EAMXFollowFragment(
                            idUser,
                            nameCompleted,
                            Image
                        )
                    )
                }
            }
            svBusarRed.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!msgGuest("buscar y seguir a otros usuarios")) {
                        search()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {}
                    return false
                }
            })
            ibBusacar.setOnClickListener {
                if (!msgGuest("buscar y seguir a otros usuarios")) {
                    search()
                }
            }
            if (!msgGuest(isMsg = false)) {
                ImagenProfile().loadImageProfile(ivUser, requireContext())
            }
            swrRefresh.setColorSchemeResources(R.color.primaryColor)
            swrRefresh.setOnRefreshListener { getAllPost() }
        }
    }

    fun selectRow(item: PostModel) {
        if (item.content.urlValidator()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.content)))
        } else {
            changeFragment(EAMXDetailFragment(), Bundle().apply {
                putParcelable(EAMXEnumUser.PUBLICATIONS.name, item)
            })
        }
    }

    fun getAllPost() {
        adapter =
            EAMXPublicationsAllAdapter(requireContext(), list, viewmodel.getSuper(), isPrincipal)
        adapter.items = arrayListOf<PostModel>()
        setupRecyclerView()
        click()
        //maximo = 0
        //showSkeleton(true)
        viewmodel.requestAllpostMi(id_user)
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
            viewmodel.requestAllpostMi(id_user, resultModel.pagination!!.next)
            binding.swrRefresh.isRefreshing = true
        } else {
            binding.swrRefresh.isRefreshing = false
        }
    }

    fun click() {
        val interactuar = "interactuar con el contenido de la red social"
        adapter.onItemClickListener = { item, Etiqueta ->
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
                                UtilAlert.ACTION_CANCEL -> {}
                            }
                        }
                        .build()
                        .show(childFragmentManager, tag)
                }
                COMENTARIO -> {
                    if (!msgGuest(interactuar)) {
                        changeFragment(EAMXComentFragment(item, list))
                    }
                }
                COMPARTIR -> {}
                LIKE -> {
                    //showSkeleton(true)
                    if (!msgGuest(interactuar)) {
                        viewmodel.reactPost(item.id)
                    }
                }
                LIKED -> {
                    if (!msgGuest(interactuar)) {
                        item.reaction?.let {
                            showSkeleton(true)
                            viewmodel.reactDel(it.id)
                        }
                    }
                }
                TEXTO -> {
                    sharetext(item.content)
                }
                IMAGEN -> {
                    // binding.rvPubliction.i_media_gallery.c_gallery.iv_thumbnail
                    val img = item.multimedia.filter {
                        it.format == "image"
                    }
                    shareimage(img)
                }
                VIDEO -> {
                    val vid = item.multimedia.filter {
                        it.format == "video/mp4"
                    }
                    sharevideo(vid)
                }
                SEGUIR -> {}
                PERFIL -> {
                    if (!msgGuest("tener un perfil en nuestra red social")) {
                        changeFragment(
                            EAMXFollowFragment(
                                item.author.id,
                                item.author.name,
                                item.author.image
                            )
                        )
                    }
                }
                "" -> {
                    if (!msgGuest(interactuar)) {
                        selectRow(item)
                    }
                }
            }
        }
    }

    private fun sharetext(text: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/*"
        share.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(share, "Compartir con"))
    }

    private fun shareimage(img: List<MultimediaModel>) {
        val imageUrls: ArrayList<String> = arrayListOf()
        img.forEach {
            imageUrls.add(it.url)
        }
        val imageUris = imageUrls.map { Uri.parse(it) }

// Crea un ArrayList para almacenar los objetos Bitmap de las miniaturas de las imágenes
        val thumbnailBitmaps = ArrayList<Bitmap>()

// Carga las miniaturas de las imágenes utilizando Glide
        for (imageUrl in imageUrls) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object : SimpleTarget<Bitmap>() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        thumbnailBitmaps.add(resource)
                        if (thumbnailBitmaps.size == imageUrls.size) {
                            // Si se han cargado todas las miniaturas, crea un Intent para compartir
                            createShareIntent(imageUris, thumbnailBitmaps)
                        }
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createShareIntent(imageUris: List<Uri>, thumbnailBitmaps: List<Bitmap>) {
        // Crea un Intent con la acción ACTION_SEND_MULTIPLE y establece el tipo de contenido a "image/*"
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            //putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
        }


        // Agrega las miniaturas de las imágenes como extras en el Intent
        val thumbnailUris = ArrayList<Uri>()
        for (thumbnailBitmap in thumbnailBitmaps) {
            val thumbnailUri = getImageUri(requireContext(), thumbnailBitmap)
            thumbnailUris.add(thumbnailUri)
        }


        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, thumbnailUris)

        startActivity(Intent.createChooser(shareIntent, "Compartir imágenes por WhatsApp"))


        for (image in thumbnailBitmaps) {
            image.recycle()
        }
    }

    private fun saveImageAndGetUri(image: Bitmap): Uri? {
        val imagesFolder = File(
            Environment.getExternalStorageDirectory(), "NombreDeTuCarpeta"
        )

        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }

        val fileName = "Imagen_${System.currentTimeMillis()}.jpg"
        val file = File(imagesFolder, fileName)

        try {
            val stream: OutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.message)
            return null
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().applicationContext.packageName}.provider",
            file
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val datetime = LocalDateTime.now()
        val bytes = ByteArrayOutputStream()
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title_${datetime.hour}_${datetime.minute}_${datetime.second}",
            null
        )
        return Uri.parse(path)
    }

    private fun sharevideo(vid: List<MultimediaModel>) {
        val vidUris: ArrayList<String> = arrayListOf()
        vid.forEach {
            vidUris.add(it.url)
        }
        val sb = StringBuilder()
        sb.append(vidUris)
        val res = sb.toString()

        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/*"
        share.putExtra(Intent.EXTRA_TEXT, res)
        startActivity(Intent.createChooser(share, "Compartir con"))
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
        binding.svBusarRed.apply {
            if (query.isNotEmpty()) {
                changeFragment(EAMXSearchFragment(), Bundle().apply {
                    putString(SEARCH, query.toString())
                })
            }
        }
    }

    fun changeFragment(fragment: Fragment, bundle: Bundle? = null) {
        NavigationFragment.Builder().apply {
            setActivity(requireActivity())
            setView(requireView().parent as ViewGroup)
            setFragment(fragment)
            if (bundle != null) {
                setBundle(bundle)
            } else {
                setAllowStack(true)
            }
            build().nextWithReplace()
        }
    }
}
