package mx.arquidiocesis.eamxgeneric.fragments.home

import android.os.Bundle
import android.view.View
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
import mx.arquidiocesis.eamxgeneric.customviews.PrayDialogFragment
import mx.arquidiocesis.eamxlivestreammodule.repository.LiveStreamRepository
import mx.arquidiocesis.eamxlivestreammodule.viewmodel.LiveVideoViewModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.EAMXViewModelProfile
import com.wallia.eamxcomunidades.ui.EAMXCommunitiesPrincipalFragment
import com.wallia.eamxcomunidades.ui.EAMXComunidadesSacerdoteFragment
import mx.arquidiocesis.eamxgeneric.databinding.ActivityMainBinding
import mx.arquidiocesis.eamxgeneric.repository.MainRepository2


class EAMXHomeFragment : EAMXBaseFragment() {

    val LOCATION_INFORMATION = "LOCATION_INFORMATION"
    val SERVICES = "SERVICES"
    val SOCIAL_NETWORKS = "SOCIAL_NETWORKS"
    val DONATIONS = "DONATIONS"
    val SOS = "SOS"
    val APPOINT_ADMINISTRATOR = "APPOINT_ADMINISTRATOR"

    val LINK = "LINK"
    val VIDEO = "VIDEO"

    var existSaint: Boolean = false
    var existRelease: Boolean = false
    var existSuggestion: Boolean = false
    var date = ""
    var isFirstTime = true

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun setViewModel() {}

    private val tokenViewModel: TokenViewModel by lazy {
        getViewModel {
            TokenViewModel(MainRepository2(requireContext()))
        }
    }

    var homeActivityBinding: ActivityMainBinding? = null

    private val viewModelProfile: EAMXViewModelProfile by lazy {
        getViewModel {
            EAMXViewModelProfile(
                RepositoryProfile(requireContext())
            )
        }
    }

    lateinit var mBinding: EamxHomeFragmentBinding
    var callBack: EAMXHome? = null
    var signOut: EAMXSignOut? = null
    var callBackBottom: EAMXActionBottom? = null
    var homeBinding: ActivityMainBinding? = null
    var userId: Int = 0

    override fun getLayout() = R.layout.eamx_home_fragment

    override fun initBinding(view: View) {
        mBinding = EamxHomeFragmentBinding.bind(view)
    }

    val viewModelLive: LiveVideoViewModel by lazy {
        getViewModel {
            LiveVideoViewModel(LiveStreamRepository(context = requireContext()))
        }
    }

    override fun initView(view: View) {
        setFullUserName()
        initOnClickListener(this.signOut!!)
        visibleModulesByProfile()
        viewModelLive.getExistsLiveVideos()
        userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        date = dateFormatString()
        getTopics()
        viewModelProfile.getUserDetailAndSaveProfile(true)
        mBinding.cardLiveEvent.visibility = View.VISIBLE //TODO : (eliminar linea cuando se desee mostrar el live)
    }

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
        tokenViewModel.dataHomeSaintResponse.observe(this) { response ->
            if (response.isNotEmpty() == true) {
                existSaint = true
            }
            mBinding.apply {
                if (response != null) {
                    if (!response.isNullOrEmpty()) {

                        tvTitleSaint.text = "SANTO DEL DÍA"
                        tvDescSaint.text = response[0].title
                        response[0].imageUrl?.let { ivSaint.loadByUrl(it) }
                        cvSaint.setOnClickListener {
                            val urlString = response[0].publishUrl
                            if (!urlString.isNullOrEmpty()) {
                                webView(urlString)
                            }
                        }
                    }
                }
            }
        }

        tokenViewModel.dataHomeReleaseResponse.observe(this) { response ->
            if (response.isNotEmpty()) {
                existRelease = true
            }

            val viewPagerAdapter = ViewPagerAdapter(response) {
                webView(it)
            }

            mBinding.viewPager.apply {
                adapter = viewPagerAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }

            mBinding.viewPagerIndicatorSuggestion.setViewPager(mBinding.viewPager)
        }

        tokenViewModel.dataHomeSuggestionResponse.observe(this) { response ->

            if (response.isNotEmpty() == true) {
                existSuggestion = true
            }

            val viewPagerAdapterSuggestion = response.let {
                ViewPagerAdapterSuggestion(it) { suggestion ->
                    if (suggestion.type == VIDEO) {
                        suggestion.article_url?.let { it1 -> openYoutubeApi(it1) }
                    } else if (suggestion.type == LINK) {
                        if (suggestion.article_url == null) {
                            activity?.supportFragmentManager?.let {
                                val dialog = PrayDialogFragment.newInstance(suggestion.id) {
                                    oracionesChange()
                                }
                                dialog.show(it, "")
                            }
                        } else {
                            val urlString = suggestion.article_url
                            if (!urlString.isNullOrEmpty()) {
                                webView(urlString!!)
                            }
                        }
                    }
                }
            }

            mBinding.viewPagerSuggestion.apply {
                adapter = viewPagerAdapterSuggestion
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            mBinding.viewPagerIndicator.setViewPager(mBinding.viewPagerSuggestion)
        }

        viewModelProfile.responseUserDetail.observe(this) { response ->
            FragmentLayoutMain.visibility = View.GONE
            //cvSaint.visibility = View.VISIBLE
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
                                    cvSaint.visibility = View.GONE
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
                                    if (existSaint) {
                                        cvSaint.visibility = View.VISIBLE
                                    }
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
                            if (existSaint) {
                                cvSaint.visibility = View.VISIBLE
                            }
                            if (existRelease) {
                                cvSuggestions.visibility = View.VISIBLE
                            }
                            if (existSuggestion) {
                                cvNews.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        if (existSaint) {
                            cvSaint.visibility = View.VISIBLE
                        }
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
                                    DONATIONS -> {

                                    }
                                    SOS -> {

                                    }
                                    APPOINT_ADMINISTRATOR -> {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        tokenViewModel.errorResponse.observe(this) {
            print("")
        }

        viewModelLive.searchInfoContent.observe(this) { response ->
            mBinding.apply {
                cardLiveEvent.setCardBackgroundColor(
                    if (response) requireContext().getColor(R.color.color_card_eventos_exist) else requireContext().getColor(
                        R.color.color_card_eventos_empty
                    )
                )
            }
        }

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

            if (existSaint) {
                cvSaint.visibility = View.VISIBLE
            }
            if (existRelease) {
                cvSuggestions.visibility = View.VISIBLE
            }
            if (existSuggestion) {
                cvNews.visibility = View.VISIBLE
            }
        }
    }

    fun getTopics() {
        tokenViewModel.getHomeRelease(
            userId,
            "RELEASE",
            date
        )
        tokenViewModel.getHomeSaint(
            userId,
            "SAINT",
            date
        )
        tokenViewModel.getHomeSuggestion(userId, "SUGGESTIONS")
    }
}
