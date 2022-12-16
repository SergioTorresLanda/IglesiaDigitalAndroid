package mx.arquidiocesis.sos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceDetalleModel(
    val id: Int,
    val creation_date: String,
    val modification_date: String,
    val funeral_home: String?,
    val address: AddressModel,
    val service: ServiceBoxModel,
    val location: LocationBoxModel,
    val priest: Priest,
    val devotee: DevoteeBoxModel,
    val support_contact:SupportModel?,
    val progress_history: List<ProgressHistoryModel>,
    val review: ReviewModel?
) : Parcelable