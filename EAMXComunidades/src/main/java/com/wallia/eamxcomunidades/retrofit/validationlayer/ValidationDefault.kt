package com.wallia.eamxcomunidades.retrofit.validationlayer

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationDefault<T> : ValidationCode<Response<T>> {
    val gson = Gson();
    override fun executeValidation(response: Response<T>) {
        if (response.code() != 200 && response.code() != 201) {
            throw BackendException("No fue posible encontrar los datos del usuario, intente más tarde.")
        }
    }
}