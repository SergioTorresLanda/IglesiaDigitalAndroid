package mx.arquidiocesis.eamxcommonutils.base

import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

open class FragmentBase : Fragment(), EAMXHome {

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

    override fun postListener(activate: Boolean) {}

    override fun restoreToolbar() {}

    override fun showToolbar(boolean: Boolean, title: String) {}
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
    ) {}

    protected fun msgGuest(msg: String = "poder acceder a este módulo", isMsg:Boolean = true): Boolean {
        var guest = eamxcu_preferences.getData(
            EAMXEnumUser.GUEST.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
        if (isMsg) {
            if (guest) {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.title_dialog_warning))
                    .setMessage("Regístrate o inicia sesión para ${msg}.")
                    .build().show(childFragmentManager, "")
            }
        }
        return guest
    }
}