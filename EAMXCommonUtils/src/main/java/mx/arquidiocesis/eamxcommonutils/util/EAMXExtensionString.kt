package mx.arquidiocesis.eamxcommonutils.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayOutputStream


val EMAIL_ADDRESS_PATTERN_VALID: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,2}" +
            ")+"
)

val PASSWORD_PATTERN_VALID: Pattern = Pattern.compile(
    "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\.=+^\\$*.&{}()?\\[\\]!\\-?@#%&/,><':;|_~`]).{8,}"
)

fun String.urlValidator(): Boolean {
    /*expresión regular*/
    val URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    val URL_PATTERN = Pattern.compile(URL_REGEX)

    val matcher: Matcher = URL_PATTERN.matcher(this)
    return matcher.matches()
}


fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && EMAIL_ADDRESS_PATTERN_VALID.matcher(this).matches()

fun String.extraIdUrlVideoYoutube(): String {
    val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"

    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(this)

    return if (matcher.find()) {
        matcher.group()
    } else {
        ""
    }
}

fun String.isUrlYoutube(): Boolean {
    val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+$"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(this)
    return matcher.find()
}
fun String.isUrlArquidiocesisComunicado(): Boolean {
    val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?arquidiocesismexico(\\.org)(\\.mx)\\/((\\d){4})\\/((\\d){2})\\/((\\d){2})\\/comunicado\\-?.+$"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(this)
    return matcher.find()
}

fun String.buildTextSuccessUrl(textNormal: String): String {

    val positionFirstHttpUrl = textNormal.indexOf("http")
    var url = ""

    val arrayTextNew: MutableMap<Int, String> = mutableMapOf()
    if (positionFirstHttpUrl > -1) {
        val arrayTextAndUrl = textNormal.substring(positionFirstHttpUrl).split(" ")
            .filter { text -> text.contains("http") }
        arrayTextAndUrl.forEach { item ->
            arrayTextNew[positionFirstHttpUrl] = item
            url = item
        }
    }
    return url
}

fun String.transformDateYYYYMMddToDDMonthNameYYYY(): String {
    val patternLetterWithInitialMonths = Pattern.compile("[efmajsond]")
    val dateReceivedString = this
    val formatReceived = "yyyy-MM-dd"
    val formatOut = "dd MMMM yyyy"

    val simpleFormat = SimpleDateFormat(formatReceived, Locale("es", "MX"))
    val dateReceived: Date? = simpleFormat.parse(dateReceivedString)
    simpleFormat.applyPattern(formatOut)
    val dateOk =
        StringBuilder().append(dateReceived?.let { simpleFormat.format(it) } ?: kotlin.run { "" })

    if (dateOk.isNotEmpty()) {
        //This comparation is when day has one digit
        when (patternLetterWithInitialMonths.matcher(dateOk.substring(3, 4).toLowerCase()).find()) {
            true -> dateOk.setRange(3, 4, dateOk.substring(3, 4).toUpperCase())
            //This comparation is when day has two digit
            false -> when (patternLetterWithInitialMonths.matcher(
                dateOk.toString().substring(2, 3).toLowerCase()
            ).find()) {
                true -> dateOk.setRange(2, 3, dateOk.substring(2, 3).toUpperCase())
                false -> print("Not funding initial data")
            }
        }
    }

    return dateOk.toString()
}

fun String.log() {
    eamxLog("arquidiosisis - $this")
}

fun String.downloadImageUrlToBitArray(context: Context, listener: (String) -> Unit) {
    Glide.with(context)
        .asBitmap()
        .load(Uri.parse(this))
        .override(300, 200)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                val baos = ByteArrayOutputStream()
                resource.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val b = baos.toByteArray()
                val temp: String = Base64.encodeToString(b, Base64.DEFAULT)
                listener(temp)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

fun String.convertToBitmap(): Bitmap? {
    return try {
        val encodeByte: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        bitmap
    } catch (e: Exception) {
        e.message
        null
    }
}

fun String.getRandomString(length: Int) : String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return this+(1..length)
        .map { charset.random() }
        .joinToString("")
}