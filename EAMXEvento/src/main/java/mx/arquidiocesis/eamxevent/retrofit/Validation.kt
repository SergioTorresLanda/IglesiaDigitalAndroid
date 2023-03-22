package mx.arquidiocesis.eamxevent.retrofit

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class Validation<T> : ValidationCode<Response<T>> {
    val gson = Gson();
    override fun executeValidation(response: Response<T>) {
        when (response.code()) {
            500, 400 -> {
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
            403 -> {
                throw BackendException("No fue posible recibir una respuesta.")
            }
        }
    }
}