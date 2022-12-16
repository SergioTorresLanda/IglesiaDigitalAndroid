package mx.arquidiocesis.eamxdonaciones.utils

import java.text.DecimalFormat

fun String.toNumber() = this.replace(" ", "")
    .replace("$","")
    .replace(",", "")
    .toDouble()

fun String.toMoneyFormat(): String? {
    val formatter = DecimalFormat("###,###,##0.00")
    return formatter.format(this.toNumber())
}