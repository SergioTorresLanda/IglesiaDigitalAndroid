package mx.arquidiocesis.eamxcommonutils.util.dialogs.blurdialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import mx.arquidiocesis.eamxcommonutils.util.dialogs.blurdialog.EAMXCUSupportBlurDialogFragmentKotlin
import mx.arquidiocesis.eamxcommonutils.util.dialogs.EAMXCUDialogViewCallback

open class EAMXCUBlurDialogFragmentEAMXCU constructor(@LayoutRes val resource: Int,
                                                      private val callbackEAMXCU: EAMXCUDialogViewCallback? =  null,
                                                      private val cancelable: Boolean = true,
                                                      private val blurLevel: Int = 8):
    EAMXCUSupportBlurDialogFragmentKotlin(){

    override fun isDimmingEnabled(isDimmingEnabled: Boolean): Boolean = isDimmingEnabled
    override fun isActionBarBlurred(isActionBarBlurred: Boolean): Boolean = isActionBarBlurred
    override fun isRenderScriptEnabled(isRenderScriptEnabled: Boolean): Boolean = isRenderScriptEnabled
    override fun blurLevel(blurlevel: Int): Int = blurLevel
    override fun isDebugEnable(debugEnabled: Boolean): Boolean = debugEnabled
    override fun downScaleFactor(downScaleFactor: Float): Float = downScaleFactor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(resource, container, false)
        // Set transparent background and no title
        isCancelable = cancelable
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        callbackEAMXCU?.onDialogViewReady(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null){
            dialog?.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 1).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun showCustom(manager: FragmentManager, tag: String?) {
        if(!this.isAdded && (this.dialog == null || !this.dialog!!.isShowing)){
            this.show(manager, tag)
        }

    }

    fun hide(){
        if(this.dialog != null && this.dialog!!.isShowing)
            this.dismiss()
    }

}