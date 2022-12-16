package mx.arquidiocesis.eamxregistromodule.ui.confirm.model

import com.google.gson.annotations.SerializedName

data class HTTPHeaders(
    val connection: String,
    @SerializedName("content-length")
    val content_length: String,
    @SerializedName("content-type")
    val content_type: String,
    val date: String,
    @SerializedName("x-amzn-requestid")
    val x_amzn_requestid: String
)