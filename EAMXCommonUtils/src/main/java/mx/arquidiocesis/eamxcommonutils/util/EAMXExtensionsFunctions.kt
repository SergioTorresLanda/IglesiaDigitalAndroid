package mx.arquidiocesis.eamxcommonutils.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.provider.MediaStore
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeStandalonePlayer
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()
fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(context, message, duration).show()

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

fun Activity.sharedLocationInGoogleMapsWithOtherApps(typeText : String,
                                                     titleInButtonSheet : String,
                                                     messageShared : String,
                                                     latitude : String,
                                                     longitude : String){
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        type = typeText
        putExtra(Intent.EXTRA_TEXT,"$messageShared \n" +
                "https://www.google.com.mx/maps?q=$latitude,$longitude")
    }
    this.startActivity((Intent.createChooser(shareIntent, titleInButtonSheet)))
}

fun Activity.sharedScreenShootWithOtherApps(
                                                view: View,
                                                typeImage : String,
                                                titleInButtonSheet : String,
                                                titleImage : String?,
                                                descriptionImage : String?){
    //Take screenShot
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache(true)
    val imageBitmap = Bitmap.createBitmap(view.drawingCache)
    view.isDrawingCacheEnabled = false

    //Get url bitmap
    val bitmapPath: String = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, titleImage, descriptionImage)
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

fun Activity.startCallTo(phone : String){
    val textTel = "tel: "
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse(textTel + phone)
    startActivity(intent)
}

fun Activity.sendEmailTo(email : String){
    val textEmail = "mailto: "
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse(textEmail + email)
    startActivity(intent)
}

fun Activity.openGoogleMapsTo(latitude : String, longitude : String){
    val gmmIntentUri = Uri.parse(
        "geo:0,0?q=" + latitude.replace(",", ".") + "," + longitude.replace(
            ",",
            "."
        )
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    startActivity(mapIntent)
}

fun Activity.openCamera(code : Int){
    this.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), code)
}

fun Activity.openGallery(code : Int, typeImage: String = "image/*"){
    this.startActivityForResult(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
            type = typeImage
        },code
    )
}

fun TextView.buildTextSuccess(textNormal : String)  {

    val positionFirstHttpUrl = textNormal.indexOf("http")

    val arrayTextNew : MutableMap<Int, String> = mutableMapOf()
    if(positionFirstHttpUrl > -1){
        val arrayTextAndUrl = textNormal.substring(positionFirstHttpUrl).split(" ").filter { text -> text.contains("http") }
        arrayTextAndUrl.forEach { item ->
            arrayTextNew[positionFirstHttpUrl] = item
        }
    }

    val spannableString = SpannableString(textNormal)

    if(arrayTextNew.isNotEmpty()) {

        val underline = UnderlineSpan().apply {
            updateDrawState(TextPaint().apply {
                color = Color.BLUE
            })
        }

        spannableString.apply {
            setSpan(
                ForegroundColorSpan(Color.BLUE),
                positionFirstHttpUrl,
                positionFirstHttpUrl + arrayTextNew[positionFirstHttpUrl]?.length!!,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                positionFirstHttpUrl,
                positionFirstHttpUrl + arrayTextNew[positionFirstHttpUrl]?.length!!,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                underline,
                positionFirstHttpUrl,
                positionFirstHttpUrl + arrayTextNew[positionFirstHttpUrl]?.length!!,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    this.text = spannableString
}

fun Activity.openYoutubeApi(urlYouTube : String){
    val intent = YouTubeStandalonePlayer.createVideoIntent(this,
        ConstansApp.ytk(), urlYouTube.extraIdUrlVideoYoutube(), 0, false, true)
    startActivity(intent)
}