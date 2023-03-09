package mx.arquidiocesis.eamxcommonutils.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.kaopiz.kprogresshud.KProgressHUD
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.databinding.EamxrLoadingViewBinding
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

@Keep
abstract class EAMXBaseFragment : Fragment() {

    private var hud: KProgressHUD? = null
    lateinit var eamxBackHandler: EAMXBackHandler
    private var listenerBackPress: EAMXBackFragment? = null
    /**
     * @author Daniel Garcia & Baudelio Andalon
     * @since 21/09/2020
     * @see Obtener el layout
     */
    abstract fun getLayout(): Int

    abstract fun initObservers()

    abstract fun setViewModel()


    /**
     * @author Daniel Garcia & Baudelio Andalon
     * @since 21/09/2020
     * @see DaggerInyection
     */
    abstract fun initDependency(savedInstanceState: Bundle?)

    /**
     * @author Daniel Garcia & Baudelio Andalon
     * @since 21/09/2020
     * @see Inicializar el binding
     */
    abstract fun initBinding(view: View)

    /**
     * @author Daniel Garcia & Baudelio Andalon
     * @since 21/09/2020
     * @see Inicializar vistas y listeners
     */
    abstract fun initView(view: View)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(getLayout(), container, false)
        initDependency(savedInstanceState)
        if (activity is EAMXBackHandler) {
            eamxBackHandler = activity as EAMXBackHandler
            if(eamxBackHandler != null)
                eamxBackHandler.setFragment(this)
        }
        setViewModel()
        initObservers()
        initBinding(view)
        initView(view)

        return view
    }

    /**
     * @author DanielGC
     * @see Muestra el progressabar
     * @param message: String?
     * @param isCancelable: Boolean
     */
    fun showProgressBarCustom(message: String? = null, isCancelable: Boolean = false){
        val view = layoutInflater.inflate(R.layout.eamxr_loading_view, null)
        val mBindingMessage = EamxrLoadingViewBinding.bind(view)
        message?.let {
            mBindingMessage.txtLoading.text = message
        }

        if (hud == null) {
            hud = KProgressHUD.create(requireContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCustomView(view)
                .setAnimationSpeed(2)
                .setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
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

    /**
     * @author DanielGC
     * @see Oculta el progressbar
     */
    fun hideProgressBarCustom(){
        eamxBackHandler.hideProgressBarCustom()
    }

    fun setBACUBackFragment(listenerBackPress: EAMXBackFragment){
        this.listenerBackPress = listenerBackPress
    }

    /**
     * @author DanielGC
     */
    fun hideKeyboard() {
        eamxBackHandler.hideKeyBoard()
    }

    fun showLottie(lottie: LottieAnimationView,
                   containerPersonality: View,
                   containerParent: View, show: Boolean){
        eamxBackHandler.showLottie(lottie,
            containerPersonality,
            containerParent, show)
    }

    /**
     * Acción que se ejecuta cuando se detecta que se preciona el boton de back de android,
     * esta funcion es llamda desde base activity
     */
    fun onFragmentBackPressed(): Boolean{
        if(listenerBackPress != null)
            return listenerBackPress?.onBackPress()!!

        return true
    }

    /**
     * Interface que se implementa en cada fragmento que requiera aplicar una
     * funcionalidad cuando se detecte que se preciono el boton de back
     */
    interface EAMXBackFragment {
        fun onBackPress(): Boolean
    }

    fun msgGuest(msg: String = "poder acceder a este módulo"): Boolean {
        var guest = eamxcu_preferences.getData(
            EAMXEnumUser.GUEST.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
        if (guest) {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Regístrate o inicia sesión para ${msg}.")
                .build().show(childFragmentManager, "")
        }
        return guest
    }
}