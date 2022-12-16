package mx.arquidiocesis.eamxcommonutils.util

import android.view.View


fun View.visibility(boolean: Boolean){
    visibility = if (boolean) View.VISIBLE else View.GONE
}