package mx.arquidiocesis.eamxcommonutils.customui.alert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.databinding.EamxProgressDialogBinding
import mx.arquidiocesis.eamxcommonutils.util.visibility

class ProgressDialog : DialogFragment() {

    private var textCancel: String? = null
    private var vBind: EamxProgressDialogBinding? = null
    private var onCancel: ((ProgressDialog) -> Unit)? = null
    private var title: String? = null
    private var progress = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = EamxProgressDialogBinding.inflate(inflater, container, false).apply {
        vBind = this
        tvTitle.visibility = View.VISIBLE
        isCancelable = false
        title?.let(tvTitle::setText)
        vBind?.pbCurrentProgress?.progress = progress
        vBind?.tvBody?.text = "$progress% descargado"
        btnAccept.visibility(textCancel != null)
        textCancel?.let(btnAccept::setText)
        btnAccept.setOnClickListener {
            onCancel?.invoke(this@ProgressDialog)
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }.root

    fun setTitle(title: String): ProgressDialog {
        this.title = title
        return this
    }


    fun setOnCancel(text: String = "Cancelar", cb: (ProgressDialog) -> Unit,): ProgressDialog {
        textCancel = text
        this.onCancel = cb
        return this
    }

    fun setProgress(progress: Int): ProgressDialog {
        lifecycleScope.launch(Dispatchers.Main) {
            this@ProgressDialog.progress = progress
            vBind?.pbCurrentProgress?.progress = progress
            vBind?.tvBody?.text = "$progress% descargado"
        }
        return this
    }


    fun show(manager: FragmentManager) {
        if (manager.findFragmentByTag(this::class.simpleName) == null)
            manager.beginTransaction()
                .add(this, this::class.simpleName)
                .commitAllowingStateLoss()
    }
}