package mx.arquidiocesis.eamxgeneric.model


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("User")
    val user: UserX?
)