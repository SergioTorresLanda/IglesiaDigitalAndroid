package mx.arquidiocesis.eamxredsocialmodule.model

import java.util.*

data class EAMXPublicationsAllRequest(
    var dateEnd: Long = Calendar.getInstance().timeInMillis,
    val dateInit: Long = 1599082993227,
    var skip: Int = 0,
    val limit: Int = 20,
    val FIIDEMPLEADO: Int = 100
) {
    companion object {
        fun getDateLong() = Calendar.getInstance().timeInMillis
    }
}
