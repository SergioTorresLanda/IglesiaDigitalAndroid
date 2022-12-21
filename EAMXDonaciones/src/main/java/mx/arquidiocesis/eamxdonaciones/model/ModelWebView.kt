package mx.arquidiocesis.eamxdonaciones.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ModelWebView(
    @SerializedName("amount")
    @Expose
    var amount: String,
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("location_id")
    @Expose
    var locationId: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("operation_id")
    @Expose
    var operationId: String,
    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String,
    @SerializedName("surnames")
    @Expose
    var surnames: String,
    @SerializedName("rfc")
    @Expose
    var rfc: String? = null,
    @SerializedName("business_name")
    @Expose
    var businessName: String? = null,
    @SerializedName("address")
    @Expose
    var address: String? = null,
    @SerializedName("neighborhood")
    @Expose
    var neighborhood: String? = null,
    @SerializedName("municipality")
    @Expose
    var municipality: String? = null,
    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null
)
