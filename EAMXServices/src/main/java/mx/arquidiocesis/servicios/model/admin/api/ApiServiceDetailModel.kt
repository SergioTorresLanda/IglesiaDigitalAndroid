package mx.arquidiocesis.servicios.model.admin.api

import android.view.View
import mx.arquidiocesis.servicios.model.admin.view.AdminDetailModel
import mx.arquidiocesis.sos.utils.Constants
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.ACCEPTED
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.PENDING_CONFIRMATION

data class ApiServiceDetailModel(
    val address: ApiAddressModel?,
    val comments: String?,
    val creation_date: String?,
    val devotee: ApiDevoteeModel?,
    val email: String?,
    val explanation: String?,
    val family_name: String?,
    val id: Int,
    val modification_date: String?,
    val person_name: String?,
    val phone: String?,
    val service: ApiServiceXModel?,
    val status: String?
)

fun ApiServiceDetailModel.mapToAdminDetailModel(): AdminDetailModel {
    return AdminDetailModel(
        id = this.id,
        nameRequest = "${this.devotee?.name} ${this.devotee?.first_surname}",
        name = this.person_name ?: "",
        family = this.family_name ?: "",
        address = "${this.address?.neighborhood}, cp ${this.address?.zipcode}, ${this.address?.description}",
        colony = this.address?.neighborhood ?: "",
        zip = this.address?.zipcode ?: "",
        email = this.email ?: "",
        serviceName = this.service?.name ?: "",
        latitude = this.address?.latitude ?: 0.0,
        longitude = this.address?.longitude ?: 0.0,
        phone = this.phone ?: "",
        explanation = this.explanation ?: "",
        isCommunionOfSick = if (this.service?.name == "Comunión a los enfermos") View.VISIBLE else View.GONE,
        isBlessingOfHome = if (this.service?.name == "Bendecir casa") View.VISIBLE else View.GONE,
        isPending = if (this.status == PENDING_CONFIRMATION) View.VISIBLE else View.GONE,
        isByClosed = if (this.status == ACCEPTED) View.VISIBLE else View.GONE
    )
}