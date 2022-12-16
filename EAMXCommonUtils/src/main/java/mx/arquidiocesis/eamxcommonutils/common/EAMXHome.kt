package mx.arquidiocesis.eamxcommonutils.common

interface EAMXHome {
    fun showToolbar(toolbarShow: Boolean, titleFragment: String)
    fun showToolbar(toolbarShow: Boolean, titleFragment: String, onActionClickListener: () -> Unit)

    fun showToolbar(
            toolbarShow: Boolean,
            titleFragment: String,
            actionText: String,
            tryGoBackListener: (goBackEvent: (Boolean) -> Unit) -> Unit,
            onActionClickListener: () -> Unit
    )

    fun restoreToolbar()

    fun postListener(activate: Boolean)
}