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

fun TextView.buildTextWithUnderscore(colorText : Int)  {

    val spannableString = SpannableString(this.text.toString())

    val underline = UnderlineSpan().apply {
        updateDrawState(TextPaint().apply {
            color = colorText
        })
    }

    spannableString.apply {
        setSpan(
                ForegroundColorSpan(colorText),
                0,
                this.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                this.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(
                underline,
                0,
                this.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    this.text = spannableString
}