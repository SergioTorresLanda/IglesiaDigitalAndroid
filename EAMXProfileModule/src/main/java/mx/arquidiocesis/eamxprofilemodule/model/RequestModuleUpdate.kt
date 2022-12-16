package mx.arquidiocesis.eamxprofilemodule.model


import com.google.gson.annotations.SerializedName

data class RequestModuleUpdate(
    @SerializedName("module_id")
    val moduleId: Int?
)