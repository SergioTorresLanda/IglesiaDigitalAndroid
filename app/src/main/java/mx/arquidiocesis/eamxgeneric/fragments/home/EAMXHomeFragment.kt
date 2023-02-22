package mx.arquidiocesis.eamxgeneric.fragments.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.adapter.ViewPagerAdapter
import mx.arquidiocesis.eamxgeneric.databinding.EamxHomeFragmentBinding
import mx.arquidiocesis.eamxgeneric.viewmodel.TokenViewModel
import com.wallia.eamxcomunidades.config.Constants
import kotlinx.android.synthetic.main.eamx_home_fragment.*
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxgeneric.adapter.ViewPagerAdapterSuggestion
import mx.arquidiocesis.eamxlivestreammodule.repository.LiveStreamRepository
import mx.arquidiocesis.eamxlivestreammodule.viewmodel.LiveVideoViewModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.EAMXViewModelProfile
import com.wallia.eamxcomunidades.ui.EAMXCommunitiesPrincipalFragment
import com.wallia.eamxcomunidades.ui.EAMXComunidadesSacerdoteFragment
import mx.arquidiocesis.eamxcommonutils.multimedia.*
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxgeneric.model.DataHomeReleaseResponse
import mx.arquidiocesis.eamxgeneric.repository.MainRepository2
import mx.arquidiocesis.oraciones.ui.DetalleOracionFragment

class EAMXHomeFragment : EAMXBaseFragment() {
    val LOCATION_INFORMATION = "LOCATION_INFORMATION"
    val SERVICES = "SERVICES"
    val SOCIAL_NETWORKS = "SOCIAL_NETWORKS"
    val DONATIONS = "DONATIONS"
    val SOS = "SOS"
    val APPOINT_ADMINISTRATOR = "APPOINT_ADMINISTRATOR"
    val LINK = "LINK"
    val PDF = "PDF"
    val FILE = "FILE"
    val IMAGE = "IMAGE"
    val VIDEO = "VIDEO"
    val AUDIO = "AUDIO"
    var existRelease: Boolean = false
    var existSuggestion: Boolean = false
    var isFirstTime = true
    lateinit var mBinding: EamxHomeFragmentBinding
    var callBack: EAMXHome? = null
    var signOut: EAMXSignOut? = null
    var callBackBottom: EAMXActionBottom? = null

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun setViewModel() {}

    //Declaración de modelos
    private val tokenViewModel: TokenViewModel by lazy {
        getViewModel {
            TokenViewModel(MainRepository2(requireContext()))
        }
    }

    private val viewModelProfile: EAMXViewModelProfile by lazy {
        getViewModel {
            EAMXViewModelProfile(
                RepositoryProfile(requireContext())
            )
        }
    }

    private val viewModelLive: LiveVideoViewModel by lazy {
        getViewModel {
            LiveVideoViewModel(LiveStreamRepository(context = requireContext()))
        }
    }

    //Declaración del layout del fragmento
    override fun getLayout() = R.layout.eamx_home_fragment

    //Declaración del binding
    override fun initBinding(view: View) {
        mBinding = EamxHomeFragmentBinding.bind(view)
    }

    override fun initView(view: View) {
        setFullUserName()
        initOnClickListener(this.signOut!!)
        visibleModulesByProfile()
        //Consultar de datos del usuario
        viewModelProfile.getUserDetailAndSaveProfile(true)
        //Para ver Si existen transmisiones
        viewModelLive.getExistsLiveVideos()
        //Consulta de contenido en carruseles
        getTopics()
    }

    //Instancia de objeto
    companion object {
        @JvmStatic
        fun newInstance(
            callBack: EAMXHome,
            callBackBottom: EAMXActionBottom,
            signOut: EAMXSignOut,
        ): EAMXHomeFragment {
            val homeFragment = EAMXHomeFragment()
            homeFragment.callBack = callBack
            homeFragment.signOut = signOut
            homeFragment.callBackBottom = callBackBottom
            return homeFragment
        }
    }

    override fun initObservers() {
        //Carruseles
        //Santo del día
        val LDHRR = MutableLiveData<List<DataHomeReleaseResponse>?>()
        LDHRR.value = null
        tokenViewModel.dataHomeSaintResponse.observe(this) { response ->
            if (!response.isNullOrEmpty()) {
                existRelease = true
            }
            tokenViewModel.dataHomeReleaseResponse.observe(this) { response1 ->
                if (!response1.isNullOrEmpty() && existRelease) {
                    LDHRR.postValue(
                        listOf(
                            response1[0], response1[1], DataHomeReleaseResponse(
                                response[0].id,
                                response[0].imageUrl,
                                response[0].startingDate,
                                response[0].publishUrl,
                                response[0].title
                            )
                        )
                    )
                } else if (existRelease) {
                    if (response1.size==2){
                        LDHRR.postValue(listOf(response1[0], response1[1]))
                    }
                    if (response1.size==1){
                        LDHRR.postValue(listOf(response1[0]))
                    }
                } else if (!response1.isNullOrEmpty()) {
                    LDHRR.postValue(
                        listOf(
                            DataHomeReleaseResponse(
                                response[0].id,
                                response[0].imageUrl,
                                response[0].startingDate,
                                response[0].publishUrl,
                                response[0].title
                            )
                        )
                    )
                }
            }
        }
        //Noticias
        LDHRR.observe(this) { response ->
            if (!response.isNullOrEmpty()) {
                existRelease = true
            }
            val viewPagerAdapter = response?.let {
                ViewPagerAdapter(it) { url ->
                    //IR a fragment: webView(new) "url" : url
                    var bundle = Bundle()
                    bundle.putString("url", url)
                    changeFragment(EAMXUrlFragment(), bundle)
                }
            }
            mBinding.viewPagerNoticias.apply {
                adapter = viewPagerAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            mBinding.viewPagerIndicatorNoticias.setViewPager(mBinding.viewPagerNoticias)
        }
        //Espiritualidad
        tokenViewModel.dataHomeSuggestionResponse.observe(this) { response ->
            if (!response.isNullOrEmpty()) {
                existSuggestion = true
            }
            val viewPagerAdapterSuggestion = response.let {
                ViewPagerAdapterSuggestion(it) { suggestion ->
                    if ((suggestion.type == LINK && suggestion.imageUrl.isNullOrEmpty() && suggestion.article_url.isNullOrEmpty()) || (suggestion.type != LINK && suggestion.article_url.isNullOrEmpty())) {
                        UtilAlert.Builder()
                            .setTitle(getString(R.string.title_dialog_warning))
                            .setMessage("Contenido no disponible")
                            .build()
                            .show(childFragmentManager, tag)
                    } else {
                        var bundle = Bundle()
                        if (suggestion.type == AUDIO) {
                            //IR a fragment: "audio" : suggestion.article_url, "titulo" : suggestion.title
                            bundle.putString("audio", suggestion.article_url)
                            bundle.putString("titulo", suggestion.title)
                            changeFragment(EAMXPlayerFragment(), bundle)
                        } else if (suggestion.type == PDF || suggestion.type == FILE) {
                            //IR a fragment: "pdf" : suggestion.article_url
                            bundle.putString("pdf", suggestion.article_url)
                            changeFragment(EAMXPdfFragment(), bundle)
                        } else if (suggestion.type == VIDEO) {
                            if (suggestion.article_url!!.isUrlYoutube()) {
                                //IR a fragment: "youtube" : suggestion.article_url, "titulo" : suggestion.title
                                bundle.putString("youtube", suggestion.article_url)
                                bundle.putString("titulo", suggestion.title)
                                openYoutubeApi(suggestion.article_url)
                                //changeFragment(EAMXYoutubeFragment(), bundle)
                            } else {
                                //IR a fragment: "video" : suggestion.article_url, "titulo" : suggestion.title
                                bundle.putString("video", suggestion.article_url)
                                bundle.putString("titulo", suggestion.title)
                                changeFragment(EAMXVideoFragment(), bundle)
                            }
                        } else if (suggestion.type == LINK) {
                            if (!suggestion.article_url.isNullOrEmpty()) {
                                //IR a fragment: "web" : suggestion.article_url
                                bundle.putString("web", suggestion.article_url)
                                changeFragment(EAMXUrlFragment(), bundle)
                            } else {
                                suggestion.id?.let { it1 -> selectItem(it1) }
                                /*suggestion.id?.let { it1 -> tokenViewModel.getPrayDetail(it1) }
                                tokenViewModel.prayResponse.observe(this) {
                                    //IR a fragment: "img" : it.image_url, "text": it.description
                                    bundle.putString("img", it.image_url)
                                    bundle.putString("text", it.description)
                                    changeFragment(EAMXTextFragment(), bundle)
                                }*/
                            }
                        } else if (suggestion.type == IMAGE) {
                            bundle.putString("img", suggestion.article_url)
                            bundle.putString("text", suggestion.title)
                            changeFragment(EAMXTextFragment(), bundle)
                        }

                    }
                }
            }
            mBinding.viewPagerEspiritualidad.apply {
                adapter = viewPagerAdapterSuggestion
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            mBinding.viewPagerIndicatorEspiritualidad.setViewPager(mBinding.viewPagerEspiritualidad)
        }

        //Configuraciones del usuario
        viewModelProfile.responseUserDetail.observe(this) { response ->
            FragmentLayoutMain.visibility = View.GONE
            mBinding.apply {
                if (response?.data?.User != null) {
                    val user = response.data.User
                    if (user.profile == Constants.COMMUNITY_RESPONSIBLE) {
                        flComunidades.background =
                            requireContext().getDrawable(R.drawable.shape_mi_iglesia_gold)
                        flRedSocial.background =
                            requireContext().getDrawable(R.drawable.shape_mi_iglesia_gold)
                        "Flow COMMUNITY_RESPONSIBLE".log()
                        if (user.community != null) {
                            "Flow COMMUNITY_RESPONSIBLE != null".log()
                            val status = user.community?.status ?: ""
                            eamxcu_preferences.saveData(
                                EAMXEnumUser.USER_COMMUNITY_ID.name,
                                user.community?.id ?: 0
                            )
                            eamxcu_preferences.saveData(
                                EAMXEnumUser.USER_COMMUNITY_STATUS.name,
                                user.community?.status ?: ""
                            )
                            eamxcu_preferences.saveData(
                                EAMXEnumUser.CHURCH.name,
                                user.community?.id ?: 0
                            )
                            when (status) {
                                Constants.PENDING_COMPLETION -> {
                                    cvSuggestions.visibility = View.GONE
                                    cvNews.visibility = View.GONE

                                    FragmentLayoutMain.visibility = View.VISIBLE
                                    horizontalScrollView.visibility = View.VISIBLE
                                    cvGo.visibility = View.VISIBLE

                                    btnGo.setOnClickListener {
                                        communityRegistro(true)
                                    }
                                }
                                Constants.PENDING_VICARAGE_APPROVAL -> {
                                    UtilAlert.Builder()
                                        .setTitle(getString(R.string.title_dialog_warning))
                                        .setMessage("Tu solicitud esta en proceso de revisión")
                                        .setListener {
                                            when (it) {
                                                UtilAlert.ACTION_ACCEPT -> {

                                                }
                                            }
                                        }
                                        .build()
                                        .show(childFragmentManager, tag)
                                }
                                else -> {
                                    if (existRelease) {
                                        cvSuggestions.visibility = View.VISIBLE
                                    }
                                    if (existSuggestion) {
                                        cvNews.visibility = View.VISIBLE
                                    }
                                }
                            }
                        } else {
                            "Flow COMMUNITY_RESPONSIBLE == NULL".log()
                            eamxcu_preferences.saveData(EAMXEnumUser.USER_COMMUNITY_ID.name, 0)
                            eamxcu_preferences.saveData(EAMXEnumUser.USER_COMMUNITY_STATUS.name, "")
                            eamxcu_preferences.saveData(EAMXEnumUser.CHURCH.name, 0)
                            if (isFirstTime && false) { //TODO : Remove false statement

                                eamxBackHandler.changeFragment(
                                    EAMXCommunitiesPrincipalFragment.newInstance(
                                        callBack!!,
                                        signOut!!,
                                        false
                                    ),
                                    R.id.FragmentLayoutMain,
                                    EAMXCommunitiesPrincipalFragment::class.java.simpleName
                                )
                                isFirstTime = false
                                FragmentLayoutMain.visibility = View.VISIBLE
                                horizontalScrollView.visibility = View.GONE
                                btnApoyar.visibility = View.GONE
                            }
                            if (existRelease) {
                                cvSuggestions.visibility = View.VISIBLE
                            }
                            if (existSuggestion) {
                                cvNews.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        if (existRelease) {
                            cvNews.visibility = View.VISIBLE
                        }
                        if (existSuggestion) {
                            cvSuggestions.visibility = View.VISIBLE
                        }
                        var permissionType =
                            eamxcu_preferences.getData(
                                EAMXEnumUser.USER_PERMISSION_TYPE.name,
                                EAMXTypeObject.STRING_OBJECT
                            ) as String
                        user.location_modules?.forEach { modulesList ->
                            modulesList.modules.forEach {
                                when (it) {
                                    LOCATION_INFORMATION -> {
                                        if (permissionType == "CHURCH") {
                                            flMiIglesia.background =
                                                requireContext().getDrawable(R.drawable.shape_mi_iglesia_gold)
                                        }
                                        if (permissionType == "COMMUNITY") {
                                            flComunidades.background =
                                                requireContext().getDrawable(R.drawable.shape_mi_iglesia_gold)
                                        }
                                    }
                                    SERVICES -> {
                                        flServicios.background =
                                            requireContext().getDrawable(R.drawable.shape_servicios_gold)
                                    }
                                    SOCIAL_NETWORKS -> {
                                        flRedSocial.background =
                                            requireContext().getDrawable(R.drawable.shape_red_social_gold)
                                    }
                                    DONATIONS -> {}
                                    SOS -> {}
                                    APPOINT_ADMINISTRATOR -> {}
                                }
                            }
                        }
                    }
                }
            }
        }

        //Error de token
        tokenViewModel.errorResponse.observe(this) {}

        //Live Streaming
        viewModelLive.searchInfoContent.observe(this) { response ->
            mBinding.apply {
                cardLiveEvent.setCardBackgroundColor(
                    if (response) requireContext().getColor(R.color.color_card_eventos_exist) else requireContext().getColor(
                        R.color.color_card_eventos_empty
                    )
                )
            }
        }

        //Configuraciones para perfil de sacerdote
        EAMXComunidadesSacerdoteFragment.isBack.observe(this) {
            val name = eamxcu_preferences
                .getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
                .toString()
            val lastName = eamxcu_preferences.getData(
                EAMXEnumUser.USER_LAST_NAME.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            callBack?.showToolbar(true, "$name $lastName")
            mBinding.FragmentLayoutMain.visibility = View.GONE
            horizontalScrollView.visibility = View.VISIBLE
            if (existRelease) {
                cvSuggestions.visibility = View.VISIBLE
            }
            if (existSuggestion) {
                cvNews.visibility = View.VISIBLE
            }
        }
    }

    fun getTopics() {
        val userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        val date = dateFormatString()
        tokenViewModel.getHomeSaint(userId, "SAINT", date)
        tokenViewModel.getHomeRelease(userId, "RELEASE", date)
        tokenViewModel.getHomeSuggestion(userId, "SUGGESTIONS")
    }

    private fun changeFragment(fragment: Fragment, bundle: Bundle) {
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setBundle(bundle)
            .setFragment(fragment)
            .setAllowStack(true)
            .build().nextWithReplace()
    }

    private fun selectItem(id: Int) {
        val bundle = Bundle()
        bundle.putInt("idOracion", id)
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setBundle(bundle)
            .setFragment(DetalleOracionFragment.newInstance())
            .setAllowStack(true)
            .build().nextWithReplace()
    }
}