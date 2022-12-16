package com.wallia.eamxcomunidades.retrofit.validationlayer

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationCommunitiesCodes<T> : ValidationCode<Response<T>> {
    val gson = Gson();
    override fun executeValidation(response: Response<T>) {
        when (response.code()) {
            500 -> {
                val itemError =
                    gson.fromJson(response.body().toString(), ResponseError::class.java)

                when (itemError.code) {
                    105 -> {
                           throw BackendException("No fue posible encontrar los datos del usuario, intente más tarde.")
                    }
                    108 -> {
                          throw BackendException("No fue posible encontrar los datos del usuario, intente más tarde.")
                    }
                }
            }
        }
    }
}