package mx.arquidiocesis.eamxcommonutils.base

import androidx.appcompat.app.AppCompatActivity
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader

open class ActivityBase : AppCompatActivity(), EAMXHome {

    private val loader by lazy { UtilLoader() }
    protected lateinit var callBack: EAMXHome

    protected fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(supportFragmentManager, tag)
        }
    }

    protected fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }

    override fun postListener(activate: Boolean) {
        TODO("Not yet implemented")
    }

    override fun restoreToolbar() {
    }

    override fun showToolbar(boolean: Boolean, title: String) {
    }

    override fun showToolbar(
        toolbarShow: Boolean,
        titleFragment: String,
        onActionClickListener: () -> Unit
    ) {
    }

    override fun showToolbar(
        toolbarShow: Boolean,
        titleFragment: String,
        actionText: String,
        tryGoBackListener: (goBackEvent: (Boolean) -> Unit) -> Unit,
        onActionClickListener: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}