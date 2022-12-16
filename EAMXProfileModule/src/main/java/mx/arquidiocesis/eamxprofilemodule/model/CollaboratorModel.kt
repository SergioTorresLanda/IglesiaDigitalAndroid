package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class CollaboratorModel(
    @SerializedName("first_surname")
    val firstSurname: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_admin")
    val isAdmin: Boolean?,
    @SerializedName("is_super_admin")
    val isSuperAdmin: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("second_surname")
    val secondSurname: String?
)