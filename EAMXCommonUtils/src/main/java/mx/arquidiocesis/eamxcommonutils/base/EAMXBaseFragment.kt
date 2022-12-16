package mx.arquidiocesis.eamxcommonutils.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader

abstract class EAMXBaseFragment<A : EAMXBaseFragment.NavigationActivity, B : ViewDataBinding>(
    @LayoutRes private val idLayout: Int
) : Fragment() {

    protected var handleActivity: A? = null
    protected lateinit var vBind: B

    abstract fun initView()

    interface NavigationActivity {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        handleActivity = context as? A
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DataBindingUtil.inflate<B>(inflater, idLayout, container, false).apply {
        vBind = this
        initView()
        this.lifecycleOwner = this@EAMXBaseFragment
    }.root

    override fun onDetach() {
        super.onDetach()
        handleActivity = null
    }

    private val loader by lazy { UtilLoader() }
    protected lateinit var callBack: EAMXHome

    protected fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    protected fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }
//
//    override fun postListener(activate: Boolean) {}
//
//    override fun restoreToolbar() {}
//
//    override fun showToolbar(boolean: Boolean, title: String) {}
//    override fun showToolbar(
//        toolbarShow: Boolean,
//        titleFragment: String,
//        onActionClickListener: () -> Unit
//    ) {
//    }
//
//    override fun showToolbar(
//        toolbarShow: Boolean,
//        titleFragment: String,
//        actionText: String,
//        tryGoBackListener: (goBackEvent: (Boolean) -> Unit) -> Unit,
//        onActionClickListener: () -> Unit
//    ) {}

}