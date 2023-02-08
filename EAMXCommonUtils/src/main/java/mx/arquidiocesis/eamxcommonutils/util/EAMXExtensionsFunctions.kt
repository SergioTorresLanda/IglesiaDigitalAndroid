package mx.arquidiocesis.eamxcommonutils.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.provider.MediaStore
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeStandalonePlayer
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import java.util.regex.Matcher
import java.util.regex.Pattern
import mx.arquidiocesis.eamxcommonutils.util.custom.CalloutLink


fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, message, duration).show()

//Abrir otra actividad mandandole parametros
inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init() //pasar parámetros
    startActivity(intent)
}

//ForResult para permisos
fun Activity.goToActivityResult(action: String, requestCode: Int, init: Intent.() -> Unit = {}) {
    val intent = Intent(action) //aquí el permiso solicitado
    intent.init()
    startActivityForResult(intent, requestCode)
}

fun Activity.sharedLocationInGoogleMapsWithOtherApps(
    typeText: String,
    titleInButtonSheet: String,
    messageShared: String,
    latitude: String,
    longitude: String
) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        type = typeText
        putExtra(
            Intent.EXTRA_TEXT, "$messageShared \n" +
                    "https://www.google.com.mx/maps?q=$latitude,$longitude"
        )
    }
    this.startActivity((Intent.createChooser(shareIntent, titleInButtonSheet)))
}

fun Activity.sharedScreenShootWithOtherApps(
    view: View,
    typeImage: String,
    titleInButtonSheet: String,
    titleImage: String?,
    descriptionImage: String?
) {
    //Take screenShot
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache(true)
    val imageBitmap = Bitmap.createBitmap(view.drawingCache)
    view.isDrawingCacheEnabled = false

    //Get url bitmap
    val bitmapPath: String = MediaStore.Images.Media.insertImage(
        contentResolver,
        imageBitmap,
        titleImage,
        descriptionImage
    )
    val bitmapUri = Uri.parse(bitmapPath)

    //Create intent
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        type = typeImage
        putExtra(Intent.EXTRA_STREAM, bitmapUri)
    }

    this.startActivity((Intent.createChooser(shareIntent, titleInButtonSheet)))
}

fun Activity.startCallTo(phone: String) {
    val textTel = "tel: "
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse(textTel + phone)
    startActivity(intent)
}

fun Activity.sendEmailTo(email: String) {
    val textEmail = "mailto: "
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse(textEmail + email)
    startActivity(intent)
}

fun Activity.openGoogleMapsTo(latitude: String, longitude: String) {
    val gmmIntentUri = Uri.parse(
        "geo:0,0?q=" + latitude.replace(",", ".") + "," + longitude.replace(
            ",",
            "."
        )
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    startActivity(mapIntent)
}

fun Activity.openCamera(code: Int) {
    this.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), code)
}

fun Activity.openGallery(code: Int, typeImage: String = "image/*") {
    this.startActivityForResult(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
            type = typeImage
        }, code
    )
}

fun TextView.buildTextSuccess(textNormal: String, context: Context) {
    val spannableString = SpannableString(textNormal)
    val hashtagSpans: ArrayList<IntArray> = getSpans(textNormal, "#\\w+")
    setSpanHashtag(spannableString, hashtagSpans, context)
    val httSpans: ArrayList<IntArray> = getSpans(
        textNormal,
        "https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)"
    )
    setSpanUrl(spannableString, httSpans, context)
    this.text = spannableString
}

private fun getSpans(body: String?, prefix: String): ArrayList<IntArray> {
    val spans = ArrayList<IntArray>()
    val pattern: Pattern = Pattern.compile(prefix)
    val matcher: Matcher = pattern.matcher(body)

    // Check all occurrences
    while (matcher.find()) {
        val currentSpan = IntArray(2)
        currentSpan[0] = matcher.start()
        currentSpan[1] = matcher.end()
        spans.add(currentSpan)
    }
    return spans
}

private fun setSpanHashtag(hashtagsContent: SpannableString, hashtagSpans: ArrayList<IntArray>, context: Context) {
    var colorLink = ContextCompat.getColor(context, R.color.blue_toast)
    for (i in 0 until hashtagSpans.size) {
        val span = hashtagSpans[i]
        val hashTagStart = span[0]
        val hashTagEnd = span[1]
        hashtagsContent.apply {
            setSpan(
                ForegroundColorSpan(colorLink),
                hashTagStart,
                hashTagEnd, 0
            )
        }
    }
}

private fun setSpanUrl(
    UrlsContent: SpannableString,
    hashtagSpans: ArrayList<IntArray>,
    context: Context
) {
    var colorLink = ContextCompat.getColor(context, R.color.blue_toast)
    for (i in 0 until hashtagSpans.size) {
        val span = hashtagSpans[i]
        val hashTagStart = span[0]
        val hashTagEnd = span[1]
        val underline = UnderlineSpan().apply {
            updateDrawState(TextPaint().apply {
                color = colorLink
            })
        }
        UrlsContent.apply {
            setSpan(
                ForegroundColorSpan(colorLink),
                hashTagStart,
                hashTagEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                hashTagStart,
                hashTagEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                underline,
                hashTagStart,
                hashTagEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                CalloutLink(context).clickableSpan,
                hashTagStart,
                hashTagEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

fun Activity.openYoutubeApi(urlYouTube: String) {
    val intent = YouTubeStandalonePlayer.createVideoIntent(
        this,
        ConstansApp.ytk(), urlYouTube.extraIdUrlVideoYoutube(), 0, false, true
    )
    startActivity(intent)
}