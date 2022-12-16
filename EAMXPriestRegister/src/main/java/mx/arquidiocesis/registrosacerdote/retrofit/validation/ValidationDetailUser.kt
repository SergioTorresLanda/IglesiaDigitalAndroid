package mx.arquidiocesis.registrosacerdote.retrofit.validation

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationDetailUser<T> : ValidationCode<Response<T>> {
    val gson = Gson()

    override fun executeValidation(response: Response<T>) {
        when (response.code()) {
            500 -> {
                val itemError =
                    gson.fromJson(response.errorBody().toString(), ResponseError::class.java)
                itemError?.let { item ->
                    when (item.code) {
                        108 -> throw BackendException("No fue posible recuperar la información del usuario, intentalo mas tarde")
                        else -> throw BackendException("No fue posible recuperar la información del usuario, intentalo mas tarde")
                    }
                }
            }
        }
    }

}