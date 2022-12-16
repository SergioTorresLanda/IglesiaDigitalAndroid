package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.api.validation

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationForgotEmail<T> : ValidationCode<Response<T>>{
    val gson  = Gson()

    override fun executeValidation(response: Response<T>) {
        when(response.code()){
            500 -> {
                val itemError = gson.fromJson(response.errorBody()?.source().toString().substring(response.errorBody()?.source().toString().indexOf("{"), response.errorBody()?.source().toString().indexOf(",")) + "}",
                        ResponseError::class.java)
                when (itemError.code) {
                    108 -> {
                        throw BackendException("Has excedido el número máximo de SMS, espera unos minutos para intentarlo de nuevo.")
                    }
                    105 -> {
                        throw BackendException("El usuario no es valido.")
                    }
                    104 -> {
                        throw BackendException("El usuario esta confirmado.")
                    }
                }
            }
            502 -> {
                throw BackendException("El código no es válido y/o la contraseña no tiene el formato correcto.")
            }
            403 -> {
                throw BackendException("No fue posible enviar el código, intentelo nuevamente.")
            }
        }
    }

}