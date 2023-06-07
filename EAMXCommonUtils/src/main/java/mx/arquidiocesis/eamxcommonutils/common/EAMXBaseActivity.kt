package mx.arquidiocesis.eamxcommonutils.common

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.databinding.EamxrLoadingViewBinding
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

abstract class EAMXBaseActivity : AppCompatActivity(), EAMXBackHandler {

    private var hud: KProgressHUD? = null
    private var eamxBaseFragment: EAMXBaseFragment? = null
    var isEnableActionButtonBackPress = true

    abstract fun initBinding(view: Int): View
    abstract fun getLayout(): Int
    abstract fun initDependency()
    abstract fun setViewModel()
    abstract fun initObservers()
    abstract fun initView()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
        setContentView(initBinding(getLayout()).rootView)
//        configToolbarDefault()
        initDependency()
        initObservers()
        initView()

        adjustFontScale(resources.configuration, 1.0f)
    }

    override fun showProgressBarCustom(message: String?, isCancelable: Boolean) {
        val view = layoutInflater.inflate(R.layout.eamxr_loading_view, null)
        val mBindingMessage = EamxrLoadingViewBinding.bind(view)
        message?.let {
            mBindingMessage.txtLoading.text = message
        }

        if (hud == null) {
            hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCustomView(view)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
                    .setDimAmount(0.6f)
        }
        if (hud != null) {

//            if(lottieView != null)
//                lottieView?.playAnimation()

            hud?.setCancellable(isCancelable)
            if (isCancelable) {
                hud?.setCancellable {
                    hideProgressBarCustom()
                }
            }

            hud?.show()
        }

    }

    private fun configToolbarDefault() {
        this.apply {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }


    override fun addFragment(fragmentEAMX: EAMXBaseFragment, containerId: Int) {
        supportFragmentManager.beginTransaction()
                .add(containerId, fragmentEAMX)
                .commitAllowingStateLoss()
    }

    override fun addFragment(fragmentEAMX: Fragment, containerId: Int) {
        supportFragmentManager.beginTransaction()
                .add(containerId, fragmentEAMX)
                .commitAllowingStateLoss()
    }

    override fun addFragment(fragment: Fragment, containerId: Int, tag: String) {
        supportFragmentManager.beginTransaction().let {
            it.add(containerId, fragment, tag)
            it.addToBackStack(tag)
            it.commit()
        }
    }

    override fun changeFragmentValidateBackStack(fragmentEAMX: Fragment, containerId: Int, TAG: String?, transition: Int?) {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(TAG)

        GlobalScope.launch(Dispatchers.Main) {
            fragment?.let {
                if (!it.isVisible && it.isAdded) {
                    if(transition != null){
                        supportFragmentManager.beginTransaction()
                            .replace(containerId, it).setTransition(transition)
                            .commitAllowingStateLoss()
                    }else {
                        supportFragmentManager.beginTransaction()
                            .replace(containerId, it)
                            .commitAllowingStateLoss()
                    }
                }else{
                    if(!TAG.isNullOrEmpty()){
                        if(transition != null) {
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it, TAG).setTransition(transition)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it, TAG)
                                .commitAllowingStateLoss()
                        }
                    }else{
                        if(transition != null) {
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it).setTransition(transition)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it)
                                .commitAllowingStateLoss()
                        }

                    }
                }
                return@launch
            }
            if(!TAG.isNullOrEmpty()){
                if(transition != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX, TAG).setTransition(transition)
                        .commitAllowingStateLoss()
                }else{
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX, TAG)
                        .commitAllowingStateLoss()
                }
            }else{
                if(transition != null){
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX).setTransition(transition)
                        .commitAllowingStateLoss()
                }else{
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX)
                        .commitAllowingStateLoss()

                }
            }
        }
    }

    override fun changeFragmentValidateBackStack(fragmentEAMX: EAMXBaseFragment, containerId: Int, TAG: String?, transition: Int?) {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(TAG)

        GlobalScope.launch(Dispatchers.Main) {
            fragment?.let {
                if (!it.isVisible && it.isAdded) {
                    if(transition != null){
                        supportFragmentManager.beginTransaction()
                            .replace(containerId, it).setTransition(transition)
                            .commitAllowingStateLoss()
                    }else {
                        supportFragmentManager.beginTransaction()
                            .replace(containerId, it)
                            .commitAllowingStateLoss()
                    }
                }else{
                    if(!TAG.isNullOrEmpty()){
                        if(transition != null) {
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it, TAG).setTransition(transition)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it, TAG)
                                .commitAllowingStateLoss()
                        }
                    }else{
                        if(transition != null) {
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it).setTransition(transition)
                                .commitAllowingStateLoss()
                        }else{
                            supportFragmentManager.beginTransaction()
                                .replace(containerId, it)
                                .commitAllowingStateLoss()
                        }

                    }
                }
                return@launch
            }
            if(!TAG.isNullOrEmpty()){
                if(transition != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX, TAG).setTransition(transition)
                        .commitAllowingStateLoss()
                }else{
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX, TAG)
                        .commitAllowingStateLoss()
                }
            }else{
                if(transition != null){
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX).setTransition(transition)
                        .commitAllowingStateLoss()
                }else{
                    supportFragmentManager.beginTransaction()
                        .replace(containerId, fragmentEAMX)
                        .commitAllowingStateLoss()

                }
            }
        }

    }

    override fun changeFragment(fragmentEAMX: EAMXBaseFragment, containerId: Int, TAG: String?) {
        supportFragmentManager.beginTransaction()
                .replace(containerId, fragmentEAMX, TAG).addToBackStack(TAG)
                .commitAllowingStateLoss()
    }

    override fun addFragmentValidateBackStack(fragmentEAMX: Fragment, containerId: Int, TAG: String?, transition: Int?) {
        if (!TAG.isNullOrEmpty()) {
            if(transition != null){
                supportFragmentManager.beginTransaction()
                    .replace(containerId, fragmentEAMX, TAG).setTransition(transition).addToBackStack(TAG)
                    .commitAllowingStateLoss()
            }else{
                supportFragmentManager.beginTransaction()
                    .replace(containerId, fragmentEAMX, TAG).addToBackStack(TAG)
                    .commitAllowingStateLoss()
            }

        } else {
            if(transition != null){
                supportFragmentManager.beginTransaction()
                    .replace(containerId, fragmentEAMX).setTransition(transition)
                    .commitAllowingStateLoss()
            }else{
                supportFragmentManager.beginTransaction()
                    .replace(containerId, fragmentEAMX)
                    .commitAllowingStateLoss()
            }

        }

    }

    override fun changeFragment(fragmentEAMX: Fragment, containerId: Int, TAG: String?) {
        supportFragmentManager.beginTransaction()
                .replace(containerId, fragmentEAMX, TAG).addToBackStack(TAG)
                .commitAllowingStateLoss()
    }


    override fun backNavigationFragment(fragment: Fragment) {
        //TODO()
    }

    override fun hideProgressBarCustom() {
//        if(lottieView != null)
//            lottieView?.pauseAnimation()

        if (hud != null && hud!!.isShowing)
            hud?.dismiss()
    }


    override fun finisActivityAndEmptyBackStack() {
        val fm = supportFragmentManager
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        finish()
    }

    override fun setFragment(EAMXBaseFragment: EAMXBaseFragment) {
        this.eamxBaseFragment = EAMXBaseFragment
    }

    override fun onBackPressed() {
        if (eamxBaseFragment != null){
            if(eamxBaseFragment?.onFragmentBackPressed()!!){
                super.onBackPressed()
                return
            }
        }


        if (isEnableActionButtonBackPress){
            super.onBackPressed()
            return
        }
    }

    override fun hideKeyBoard() {
        if (currentFocus != null) {
            val inputMethodManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun showKeyBoard(viewEditable: View) {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(viewEditable, InputMethodManager.SHOW_IMPLICIT)
        }
    }
    /**
     * MUESTRA U OCULTA EL LOTTIE
     */
    override fun showLottie(
            lottie: LottieAnimationView,
            containerPersonality: View,
            containerParent: View, show: Boolean
    ) {
        if (show) {
            isEnableActionButtonBackPress = false
            hideKeyBoard()
            lottie.visibility = View.VISIBLE
            containerPersonality.visibility = View.GONE
            containerParent.setBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            R.color.white
                    )
            )
        } else {
            isEnableActionButtonBackPress = true
            lottie.visibility = View.GONE
            containerPersonality.visibility = View.VISIBLE
            containerParent.setBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            R.color.gris_super_bajo
                    )
            )
        }
    }


    /**
     * Funcion que anula la configuracion del sistema para cambiar el tamaño de las fuente
     */
    private fun adjustFontScale(configuration: Configuration, scale: Float) {
        configuration.fontScale = scale
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    protected fun msgGuest(msg: String = "poder acceder a este módulo", isMsg:Boolean = true): Boolean {
        val guest = eamxcu_preferences.getData(
            EAMXEnumUser.GUEST.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
        if (isMsg) {
            if (guest) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage("Regístrate o inicia sesión para ${msg}.")
                    .setTextButtonCancel("Ahora no").setTextButtonOk("Ir al registro")
                    .setListener { action ->
                        when (action) {
                            UtilAlert.ACTION_ACCEPT -> {
                                Log.e("ZOROASTRO","SE VA A LOGIN Y DE BUENAS")
                                (this as EAMXSignOut).signOut(true)
                            }
                            UtilAlert.ACTION_CANCEL -> {
                            }
                        }
                    }
                    .build().show(supportFragmentManager, "")
            }
        }
        return guest
    }
}