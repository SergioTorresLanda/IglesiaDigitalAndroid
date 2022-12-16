package mx.arquidiocesis.registrosacerdote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriestRegisterModel(
    var name: String,
    var firstSurname: String,
    var secondSurname: String,
    var description: String,
    var birthDate: String,
    var ordinationDate: String,
    var email: String,
    var activities: List<ActivitiesModel>,
    var stream: String,
) : Parcelable
