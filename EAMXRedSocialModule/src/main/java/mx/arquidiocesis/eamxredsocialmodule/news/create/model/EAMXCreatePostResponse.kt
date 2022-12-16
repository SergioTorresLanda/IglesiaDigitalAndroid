package mx.arquidiocesis.eamxredsocialmodule.news.create.model

import com.google.gson.JsonObject

data class EAMXCreatePostResponse(
    val message: String?,

    val status: String?,

    val data: JsonObject?,
)