package mx.arquidiocesis.eamxgeneric.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.contrasresult.TakePhoto
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.ActivityMainBinding
import mx.arquidiocesis.eamxgeneric.fragments.home.EAMXHomeFragment
import mx.arquidiocesis.eamxgeneric.model.TokenObj
import mx.arquidiocesis.eamxgeneric.viewmodel.TokenViewModel
import mx.arquidiocesis.eamxprofilemodule.ui.EAMXContenedorPrincipalFragment
import mx.arquidiocesis.eamxprofilemodule.ui.profile.EAMXProfilePrincipalFragment
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxDialogClosePostBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.EAMXCreateFragment
import mx.arquidiocesis.sos.ui.FaithfulProfileFragment
import mx.arquidiocesis.sos.ui.PriestProfileFragment
import mx.arquidiocesis.sos.ui.SOSNotificationFaithfulFragment
import mx.arquidiocesis.sos.ui.SOSProfileFragment
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile
import mx.arquidiocesis.eamxgeneric.repository.MainRepository2
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXRedSocialFragment
import java.util.*
import java.util.concurrent.TimeUnit

class EAMXHomeActivity : EAMXBaseActivity(),
    EAMXHome,
    EAMXSignOut,
    EAMXActionBottom,
    EAMXComunicationCreateFragment,
    EAMXProfilePrincipalFragment.NavigationActivity{

    private lateinit var registerCamera: ActivityResultLauncher<Context>
    private var cancelOnBack = false
    val homeFragment = EAMXHomeFragment.newInstance(this, this, this)
    val perfilFragment = EAMXContenedorPrincipalFragment.newInstance()
    val sosFragment = SOSProfileFragment.newInstance(this)

    private val tokenViewModel: TokenViewModel by lazy {
        getViewModel {
            TokenViewModel(MainRepository2(context = applicationContext))
        }
    }
    lateinit var mBinding: ActivityMainBinding
    lateinit var showDialogDescarte: () -> Boolean
    lateinit var tokenStr: String

    var isMenu = false
    var globalBinding = null

    private lateinit var name: String
    private lateinit var lastName: String
    private lateinit var nameCompleted: String
    private lateinit var userRole: String
    private var tokenSaved: Boolean
    private var userId: Int
    private var userProfile: String

    init {
        name =
            eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
                .toString()
        lastName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_LAST_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        nameCompleted = "$name $lastName"
        userRole = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ROLE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        tokenSaved =
            eamxcu_preferences.getData("TOKENSAVED", EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        userId =
            eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
        userProfile = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PROFILE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
    }

    override fun setViewModel() {}

    override fun getLayout() = R.layout.activity_main

    override fun initBinding(view: Int): View {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initDependency() {}

    override fun initObservers() {
        tokenViewModel.sendTokenResponse.observe(this) {
            eamxcu_preferences.saveData("TOKENSAVED", true)
            eamxcu_preferences.saveData("TOKENFIREBASE", tokenStr)
        }

        tokenViewModel.deleteTokenResponse.observe(this) {
            eamxcu_preferences.saveData("TOKENSAVED", false)
            eamxcu_preferences.saveData("TOKENFIREBASE", "")
        }

        tokenViewModel.errorResponse.observe(this) {
            print("")
        }


        /*MyFirebaseMessagingService.notification.observe(this) {
            val sosFragment = SOSProfileFragment.newInstance(this)

            NotificationFragment(it.notification?.title!!, it.notification?.body!!) {
                if ((it as String) == "REFUSED") {
                    changeFragmentValidateBackStack(
                        sosFragment,
                        R.id.contentFragment,
                        SOSProfileFragment::class.java.simpleName
                    )
                } else {
                    changeFragmentValidateBackStack(
                        sosFragment,
                        R.id.contentFragment,
                        SOSProfileFragment::class.java.simpleName
                    )
                }
            }.show(supportFragmentManager, null)
        }*/
    }

    override fun initView() {
        registerCamera = registerForActivityResult(TakePhoto()){onResultPickerCamera?.invoke(it)}
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        mBinding.toolbar.txtSaludo.text = saludoUsuario()
        mBinding.toolbar.txtNameUser.text = nameCompleted

        ImagenProfile().loadImageProfile(mBinding.toolbar.imgUser, this)

        if (!tokenSaved) {
            initFirebase()
        }

        addFragment(homeFragment, R.id.contentFragment)

        mBinding.toolbar.txtNameUser.setOnClickListener {
            changeFragmentValidateBackStack(
                perfilFragment,
                R.id.contentFragment,
                EAMXContenedorPrincipalFragment::class.java.simpleName
            )
        }

        mBinding.toolbar.imgNotification.setOnClickListener {
            eamxcu_preferences.saveData("FROMSOS", false)
            changeFragmentValidateBackStack(
                sosFragment,
                R.id.contentFragment,
                SOSProfileFragment::class.java.simpleName
            )
        }

        mBinding.apply {
            bottomNavigation.itemIconTintList = null
            bottomNavigation.setOnNavigationItemSelectedListener {

                isMenu = true

                when (it.itemId) {
                    R.id.homeFragment -> {
                        changeFragmentValidateBackStack(
                            homeFragment,
                            R.id.contentFragment,
                            EAMXHomeFragment::class.java.simpleName
                        )
                    }
                    R.id.helpFragment -> {
                        eamxcu_preferences.saveData("FROMSOS", true)
                        changeFragment(
                            sosFragment,
                            R.id.contentFragment,
                            SOSProfileFragment::class.java.simpleName
                        )
                    }
                    R.id.perfilFragment -> {
                        changeFragment(
                            perfilFragment,
                            R.id.contentFragment,
                            EAMXContenedorPrincipalFragment::class.java.simpleName
                        )
                    }
                }
                true
            }
        }

        //Si es la primerva vez que se utiliza la app se envía al usuario directo al perfil
        val isFirstTimeOpenApp = eamxcu_preferences.getData(
            EAMXEnumUser.USER_NEED_COMPLETE_PROFILE.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean

        if (isFirstTimeOpenApp) {
            changeFragmentValidateBackStack(
                perfilFragment,
                R.id.contentFragment,
                EAMXContenedorPrincipalFragment::class.java.simpleName
            )
        }
    }

    fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            tokenStr = token.toString()
            tokenViewModel.sendToken(
                userId,
                TokenObj(tokenStr)
            )
        })

    }

    override fun restoreToolbar() {
//        toolbarHome()
    }

    override fun showToolbar(toolbarShow: Boolean, titleFragment: String) {
        bottomNavigation.visibility = View.VISIBLE
        cancelOnBack = false
        if (toolbarShow) {
            "NAME USER $name".log()
            "titleFragment $titleFragment".log()
            if (titleFragment.contains(name)) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = resources.getColor(R.color.white)
                mBinding.apply {
                    toolbarHome()
                    toolbar.txtNameUser.text = titleFragment
                    bottomNavigation.menu[0].isChecked = true
                }
            } else {
                when (titleFragment) {
                    nameCompleted -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        window.statusBarColor = resources.getColor(R.color.white)
                        mBinding.apply {
                            toolbarHome()
                            bottomNavigation.menu[0].isChecked = true
                        }

                    }
                    AppMyConstants.home -> {
                        cancelOnBack = false
                        bottomNavigation.visibility = View.VISIBLE
                        bottomNavigation.menu[0].isChecked = true
                        bottomNavigation.selectedItemId = R.id.homeFragment
                        changeFragmentValidateBackStack(
                            EAMXHomeFragment.newInstance(this, this, signOut = this),
                            R.id.contentFragment,
                            EAMXHomeFragment::class.java.simpleName
                        )
                    }
                    AppMyConstants.cadenaOracion -> toolbarBlue(titleFragment)
                    AppMyConstants.sos -> {
                        toolbarBlue(titleFragment)
                        bottomNavigation.menu[1].isChecked = true
                    }
                    AppMyConstants.eventos_vivo -> toolbarBlue(titleFragment)
                    AppMyConstants.formacion -> toolbarBlue(titleFragment)
                    AppMyConstants.miIglesia -> toolbarBlue(titleFragment)
                    AppMyConstants.oraciones -> toolbarBlue(titleFragment)
                    AppMyConstants.servicios -> toolbarBlue(titleFragment)
                    "Bendecir casa" -> toolbarBlue(titleFragment)
                    "Comunión a los enfermos" -> toolbarBlue(titleFragment)
                    AppMyConstants.notificaciones -> toolbarBlue(titleFragment)
                    AppMyConstants.comentarios -> toolbarBlue(titleFragment)
                    AppMyConstants.intentions -> toolbarBlue(titleFragment)
                    AppMyConstants.comunidad -> toolbarBlue(titleFragment)
                    AppMyConstants.comunidadAdd -> {
                        toolbarHome(isVisibleConfig = false)
                        bottomNavigation.visibility = View.GONE
                        cancelOnBack = true
                    }
                    AppMyConstants.editarComunidad -> toolbarBlue(titleFragment)
                    AppMyConstants.intentionsCommunity -> toolbarBlue(titleFragment)
                    AppMyConstants.ofrendaTarjeta -> {
                    }
                    AppMyConstants.ofrenda -> toolbarBlue(titleFragment)
                        "" -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        window.statusBarColor = resources.getColor(R.color.white)
                        mBinding.apply {
                            toolbarHome()
                            toolbar.txtNameUser.text = titleFragment
                            bottomNavigation.menu[0].isChecked = true
                        }
                    }
                    else -> {
                        toolbarBlueGeneral(titleFragment)
                    }
                }
            }

        } else {
            when (titleFragment) {
                AppMyConstants.red_social -> {
                    bottomNavigation.menu[0].isChecked = true
                }
                else -> {
                    bottomNavigation.menu[2].isChecked = true

                }
            }

            mBinding.toolbar.constraintToolbar.visibility = View.GONE
        }
    }

    override fun showToolbar(
        toolbarShow: Boolean,
        titleFragment: String,
        onActionClickListener: () -> Unit
    ) {
        if (toolbarShow) {
            toolbarBlue(titleFragment, onActionClickListener)

        } else {
            mBinding.toolbar.constraintToolbar.visibility = View.GONE
            //TODO TENTATIVE se ejecuta dos veces el profile
            bottomNavigation.menu[2].isChecked = true
        }
    }

    override fun showToolbar(
        toolbarShow: Boolean, titleFragment: String, actionText: String,
        tryGoBackListener: (goBackEvent: (Boolean) -> Unit) -> Unit,
        onActionClickListener: () -> Unit
    ) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.white)

        mBinding.toolbar.apply {
            constraintToolbar.visibility = View.VISIBLE
            toolbarWhiteSmall.visibility = View.VISIBLE
            toolbarBlue.visibility = View.GONE
            toolbarGeneral.visibility = View.GONE
            toolbarHomeSaludo.visibility = View.GONE
            constraintToolbar.setBackgroundResource(R.drawable.shape_white_toolbar)

            txtTitleFragmentWhite.text = titleFragment
            txtTitleFragmentWhite.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.gray_title
                )
            )
            btnBackSmall.setImageResource(R.drawable.ic_cancel)
            btnBackSmall.setOnClickListener {
                tryGoBackListener() { goBack ->
                    if (goBack) {
                        btnBackSmall.setImageResource(R.drawable.ic_btn_back_two)
                        super.onBackPressed()
                    }
                }
            }

            btnToPost.visibility = View.VISIBLE
            btnToPost.setTextColor(ContextCompat.getColor(root.context, R.color.grayLight))
            btnToPost.setOnClickListener {
                btnToPost.isEnabled = false
                Observable.timer(2, TimeUnit.SECONDS)
                    .flatMap {
                        return@flatMap Observable.create<String> { emitter ->
                            emitter.onNext("ok")
                            emitter.onComplete()
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        btnToPost.isEnabled = true
                    }
                onActionClickListener()
            }
            btnToPost.text = actionText
        }
    }

    override fun postListener(activate: Boolean) {
        mBinding.toolbar.btnToPost.isEnabled = activate
        mBinding.toolbar.btnToPost.setTextColor(
            ContextCompat.getColor(
                this,
                if (activate) R.color.orange else R.color.grayLight
            )
        )
    }

    fun toolbarHome(isVisibleConfig: Boolean = true) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.white)
        mBinding.toolbar.apply {

            ImagenProfile().loadImageProfile(imgUser, this@EAMXHomeActivity)

            constraintToolbar.visibility = View.VISIBLE
            toolbarHomeSaludo.visibility = View.VISIBLE
            toolbarBlue.visibility = View.GONE
            toolbarGeneral.visibility = View.GONE
            toolbarWhiteSmall.visibility = View.GONE

            ivConfig.apply {
                visibility = if (isVisibleConfig) View.VISIBLE else View.GONE
                setOnClickListener {
                    changeFragment(
                        perfilFragment,
                        R.id.contentFragment,
                        EAMXContenedorPrincipalFragment::class.java.simpleName
                    )
                }
            }

            if (userProfile == EAMXProfile.DevotedAdmin.rol || userProfile == EAMXProfile.PriestAdmin.rol) {
                constraintToolbar.setBackgroundResource(R.drawable.shape_blue_profile)
                txtNameUser.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.white
                    )
                )
                txtSaludo.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.white
                    )
                )
                tvAdmin.visibility = View.VISIBLE
            } else {
                constraintToolbar.setBackgroundResource(R.drawable.shape_white_toolbar)
            }
        }
    }

    fun toolbarBlue(titleFragment: String) {
        window.statusBarColor = resources.getColor(R.color.primaryColor)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        mBinding.toolbar.apply {

            constraintToolbar.visibility = View.VISIBLE
            constraintToolbar.setBackgroundResource(R.drawable.shape_blue_toolbar)
            toolbarHomeSaludo.visibility = View.GONE
            toolbarWhiteSmall.visibility = View.GONE
            toolbarGeneral.visibility = View.GONE
            toolbarBlue.visibility = View.VISIBLE
            ivIconToobar.visibility = View.GONE
            txtTitleFragmentBlue.setTextColor(
                ContextCompat.getColor(
                    this@EAMXHomeActivity,
                    R.color.white
                )
            )
            txtTitleFragmentBlue.text = titleFragment
            btnBackBlue.setOnClickListener { onBackPressed() }
        }
    }

    fun toolbarBlueGeneral(titleFragment: String) {
        window.statusBarColor = resources.getColor(R.color.primaryColor)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        mBinding.toolbar.apply {

            constraintToolbar.visibility = View.VISIBLE
            constraintToolbar.setBackgroundResource(R.drawable.shape_blue_toolbar)
            toolbarHomeSaludo.visibility = View.GONE
            toolbarWhiteSmall.visibility = View.GONE
            toolbarBlue.visibility = View.GONE
            toolbarGeneral.visibility = View.VISIBLE
            ivIconToobar.visibility = View.GONE
            txtTitleFragmentGeneral.setTextColor(
                ContextCompat.getColor(
                    this@EAMXHomeActivity,
                    R.color.white
                )
            )
            txtTitleFragmentGeneral.text = titleFragment
            btnBackGeneral.setOnClickListener { onBackPressed() }
        }
    }


    fun toolbarBlue(titleFragment: String, onActionClickListener: () -> Unit) {
        window.statusBarColor = resources.getColor(R.color.primaryColor)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        mBinding.toolbar.apply {
            constraintToolbar.visibility = View.VISIBLE
            constraintToolbar.setBackgroundResource(R.drawable.shape_blue_toolbar)
            toolbarHomeSaludo.visibility = View.GONE
            toolbarWhiteSmall.visibility = View.GONE
            toolbarBlue.visibility = View.VISIBLE
            toolbarGeneral.visibility = View.GONE
            txtTitleFragmentBlue.setTextColor(
                ContextCompat.getColor(
                    this@EAMXHomeActivity,
                    R.color.white
                )
            )
            txtTitleFragmentBlue.text = titleFragment
            btnBackBlue.setOnClickListener { onBackPressed() }
            ivIconToobar.visibility = View.VISIBLE
            ivIconToobar.setOnClickListener {
                onActionClickListener()
            }
        }
    }

    fun toolbarWhiteSmall(titleFragment: String) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.white)

        mBinding.toolbar.apply {
            constraintToolbar.setBackgroundResource(R.drawable.shape_white_toolbar)
            toolbarHomeSaludo.visibility = View.GONE
            toolbarBlue.visibility = View.GONE
            toolbarGeneral.visibility = View.GONE
            toolbarWhiteSmall.visibility = View.VISIBLE
            txtTitleFragmentWhite.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.primaryColor
                )
            )
            txtTitleFragmentWhite.text = titleFragment
            btnToPost.visibility = View.GONE
            btnBackSmall.setImageResource(R.drawable.ic_btn_back_two)
            btnBackSmall.setOnClickListener { onBackPressed() }

        }
    }

    fun saludoUsuario(): String {
        val cal = Calendar.getInstance()
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        var saludo = ""

        saludo = when (hours) {
            in 7..11 -> getString(R.string.good_morning)
            in 12..19 -> getString(R.string.good_afternon)
            else -> getString(R.string.good_nigth)
        }
        return saludo
    }

    override fun signOut(closeSession: Boolean) {
        toast("Sesión cerrada")

        tokenViewModel.deleteToken(
            eamxcu_preferences.getData(
                "TOKENFIREBASE",
                EAMXTypeObject.STRING_OBJECT
            ) as String
        )

        EAMXSplashActivity.signOutFromPerfil = true
        val intent = Intent(this, EAMXSplashActivity::class.java)
        eamxcu_preferences.saveData(EAMXEnumUser.SESSION.name, false)
        eamxcu_preferences.saveData(EAMXEnumUser.USER_ROLE.name, "")
        //eamxcu_preferences.removeFile()

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun actionOnBackPressed(onBackPressed: () -> Boolean) {
        showDialogDescarte = onBackPressed
    }

    fun showDialogValidate() {
        val dialogView = layoutInflater.inflate(R.layout.eamx_dialog_close_post, null, false)
        val dialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).setView(dialogView)
            .setCancelable(false).show()
        dialogView?.let { dialogViewBinding ->
            val dialogBinding = EamxDialogClosePostBinding.bind(dialogViewBinding)
            dialogBinding.tvContinue.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.tvClose.setOnClickListener {
                dialog.dismiss()
                mBinding.bottomNavigation.visibility = View.VISIBLE
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun hideBottom() {
        mBinding.bottomNavigation.visibility = View.GONE
    }

    override fun showBottom() {
        mBinding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onBackPressed() {

        if (cancelOnBack) {
            return
        }

        hideKeyBoard()
        var fragmentTag = supportFragmentManager.findFragmentById(R.id.contentFragment)
        var fragmentTagRedSocial =
            supportFragmentManager.findFragmentById(R.id.contentFragmentRedSocial)
        var fragmentTagSOS =
            supportFragmentManager.findFragmentById(R.id.frameSOSProfile)

        val fragmentAdminModules =
            supportFragmentManager.findFragmentById(R.id.content)

        var fromSOS = eamxcu_preferences.getData(
            "FROMSOS",
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
//        var fragmentOraciones = supportFragmentManager.findFragmentById(R.id.conten)
        when (fragmentTag) {
            !is EAMXHomeFragment -> {
                if ((fragmentTagRedSocial is EAMXCreateFragment && showDialogDescarte()) && isMenu) {
                    isMenu = false
                    showDialogValidate()
                } else if ((fragmentTagRedSocial is EAMXRedSocialFragment) && isMenu) {
                    isMenu = false
                    changeFragmentValidateBackStack(
                        EAMXHomeFragment.newInstance(this, this, signOut = this),
                        R.id.contentFragment,
                        EAMXHomeFragment::class.java.simpleName
                    )
                } else if ((fragmentAdminModules is EAMXProfilePrincipalFragment) && isMenu) {
                    isMenu = false

                    changeFragmentValidateBackStack(
                        EAMXHomeFragment.newInstance(this, this, signOut = this),
                        R.id.contentFragment,
                        EAMXHomeFragment::class.java.simpleName
                    )
                } else if ((fragmentTagSOS is FaithfulProfileFragment) && isMenu) {
                    isMenu = false

                    changeFragmentValidateBackStack(
                        EAMXHomeFragment.newInstance(this, this, signOut = this),
                        R.id.contentFragment,
                        EAMXHomeFragment::class.java.simpleName
                    )
                } else if ((fragmentTagSOS is PriestProfileFragment) && isMenu) {
                    isMenu = false

                    changeFragmentValidateBackStack(
                        EAMXHomeFragment.newInstance(this, this, signOut = this),
                        R.id.contentFragment,
                        EAMXHomeFragment::class.java.simpleName
                    )
                } else if (fragmentTagSOS is SOSNotificationFaithfulFragment && !fromSOS && isMenu) {
                    isMenu = false

                    changeFragmentValidateBackStack(
                        EAMXHomeFragment.newInstance(this, this, signOut = this),
                        R.id.contentFragment,
                        EAMXHomeFragment::class.java.simpleName
                    )
                } else {
                    try {
                        super.onBackPressed()
                        mBinding.bottomNavigation.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
                mBinding.apply {
                    /*if (framePrincipalLocal.visibility == View.VISIBLE) {
                        clViewGenmeral.visibility = View.VISIBLE
                        framePrincipalLocal.visibility = View.GONE
                    } else {
                        finish()
                    }*/
                }
            }
        }
    }

    private var onResultPickerCamera: ((Uri?) -> Unit)? = null

    override fun launchCamera(cb: (Uri?) -> Unit) {
        this.onResultPickerCamera = cb
        registerCamera.launch(this)
    }


}