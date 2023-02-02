package mx.arquidiocesis.eamxcommonutils.util.custom

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast


class CalloutLink(ctx: Context) : ClickableSpan() {
    var context: Context? = ctx

    override fun updateDrawState(ds: TextPaint) {
        ds.setARGB(1, 0, 0, 255)
        ds.color = Color.BLUE
    }

    override fun onClick(widget: View) {
        val tv = widget as TextView
        val s = tv.text as Spanned
        val start = s.getSpanStart(this)
        val end = s.getSpanEnd(this)
        val theWord = s.subSequence(start + 1, end).toString()
        val uri = Uri.parse(theWord)
        val i = Intent(Intent.ACTION_VIEW, uri)
        context!!.startActivity(i)
    }
}