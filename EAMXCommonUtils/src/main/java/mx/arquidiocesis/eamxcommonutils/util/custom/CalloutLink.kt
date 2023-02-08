package mx.arquidiocesis.eamxcommonutils.util.custom

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import mx.arquidiocesis.eamxcommonutils.R


class CalloutLink(ctx: Context) {
    var context: Context? = ctx


    val clickableSpan = object : ClickableSpan(){
        override fun onClick(widget: View){
            val tv = widget as TextView
            val s = tv.text as Spanned
            val start = s.getSpanStart(this)
            val end = s.getSpanEnd(this)
            val theWord = s.subSequence(start, end).toString()
            val uri = Uri.parse(theWord)
            val i = Intent(Intent.ACTION_VIEW, uri)
            context!!.startActivity(i)
            val f = ForegroundColorSpan(Color.GRAY)
            f.foregroundColor
        }
//"https://desdelafe.mx/noticias/sabias-que/salmo-91-la-oracion-que-puedes-rezar-cada-dia-para-pedir-proteccion/"

        override fun updateDrawState(ds:TextPaint) {
            ds.setARGB(1, 0, 0, 255)
            ds.color = Color.RED
            ds.isUnderlineText = true

        }
    }
}
