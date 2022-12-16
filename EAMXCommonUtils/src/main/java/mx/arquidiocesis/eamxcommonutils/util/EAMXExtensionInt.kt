package mx.arquidiocesis.eamxcommonutils.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun Int.numberToMonthName() : String {
    return when (this) {
        0 -> "Enero"
        1 -> "Febrero"
        2 -> "Marzo"
        3 -> "Abril"
        4 -> "Mayo"
        5 -> "Junio"
        6 -> "Julio"
        7 -> "Agosto"
        8 -> "Septiembre"
        9 -> "Octubre"
        10 -> "Noviembre"
        11 -> "Diciembre"
        else -> ""
    }
}

fun Int.numberToDayNameCompleted() : String {
    return when (this) {
        0 -> "Domingo"
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        else -> ""
    }
}

fun Int.numberToDayFirstLetter() : String {
    return when (this) {
        0 -> "D"
        1 -> "L"
        2 -> "M"
        3 -> "X"
        4 -> "J"
        5 -> "V"
        6 -> "S"
        else -> ""
    }
}

fun Int.convertDrawableToBitmapDescriptionForMarketMap(context : Context, width : Int, height : Int) : BitmapDescriptor {
    val bitmapDraw = context.applicationContext?.let {
        ContextCompat.getDrawable(
            it,
            this
        )
    } as BitmapDrawable

    val smallMarker =
        Bitmap.createScaledBitmap(bitmapDraw.bitmap, width, height, false)
   return BitmapDescriptorFactory.fromBitmap(smallMarker)
}