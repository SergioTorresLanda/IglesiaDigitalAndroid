package mx.upax.formacion.model

import com.upax.formacion.R
import java.text.DecimalFormat
import java.text.NumberFormat

open class BaseModel(
    val id: Int,
    val image: String,
    val title: String,
    val subtitle: String,
    val tags: String,
    val views: Int,
    val type: String,
    val url: String
){

    private var viewFormat : String = ""
        get() = getViewsFormat()


    fun getViewsFormat() : String{
        val format: NumberFormat = DecimalFormat("#,###")
       return format.format(views).plus(" vistas")
    }
}