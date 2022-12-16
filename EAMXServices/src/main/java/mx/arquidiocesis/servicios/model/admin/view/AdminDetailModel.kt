package mx.arquidiocesis.servicios.model.admin.view

import android.view.View

data class AdminDetailModel(
    val id : Int = 0,
    val nameRequest : String= "",
    val name : String= "",
    val family : String= "",
    val address : String= "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val colony : String= "",
    val zip : String= "",
    val email : String= "",
    val serviceName : String= "",
    val phone : String= "",
    val explanation : String = "",
    val isPending : Int = View.GONE,
    val isByClosed : Int = View.GONE,
    val isBlessingOfHome : Int = View.GONE,
    val isCommunionOfSick : Int = View.GONE,
)