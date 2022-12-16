package mx.arquidiocesis.eamxdonaciones.model

import com.google.gson.annotations.SerializedName

data class BillingModel(
    var id: Int?=null,

    @SerializedName("business_name", alternate = ["business_Name"])
    var business_Name: String?=null,
    var rfc: String?=null,
    var address: String?=null,
    var neighborhood: String?=null,
    var zipcode: String?=null,
    var municipality: String?=null,
    var email: String?=null,
    var automatic_invoicing: Boolean?=null

)