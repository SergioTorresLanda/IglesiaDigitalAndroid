package  mx.arquidiocesis.eamxcommonutils.customui.loader

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import mx.arquidiocesis.eamxcommonutils.R

class UtilLoader : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        isCancelable = false

        return  inflater.inflate(R.layout.loader_fragment, container, false)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
            manager.executePendingTransactions()
        } catch (ignored: IllegalStateException) {
            ignored.printStackTrace()
        }catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

}