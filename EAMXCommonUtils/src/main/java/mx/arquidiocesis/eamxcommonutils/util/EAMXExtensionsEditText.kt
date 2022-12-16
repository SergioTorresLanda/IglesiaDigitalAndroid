package mx.arquidiocesis.eamxcommonutils.util

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.squareup.picasso.Picasso
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

//Función para pasar al siguiente edittext
fun EditText.nextFocusEditText(prevFocus: EditText? = null, nextFocus: EditText? = null) {
    this.isLongClickable = false
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            prevFocus?.let {
                if (count == 0) {
                    if (it.text.toString().isNotEmpty()) {
                        it.setSelection(1)
                    } else {
                        it.setSelection(0)
                    }
                    it.requestFocus()
                }
            }
            nextFocus?.let {
                when (count) {
                    1 -> {
                        it.requestFocus()
                    }
                    else -> {

                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            nextFocus?.let {
                if (s.toString().length > 1) {
                    this@nextFocusEditText.setText(s.toString().substring(1))
                    it.requestFocus()
                }
            }
        }
    })
}

fun EditText.deleteFocusEditText() {
    this.setOnKeyListener { _, keyCode, keyEvent ->
        if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
            this.setText("")
        }
        false
    }
}
