package mx.arquidiocesis.registrosacerdote.retrofit.validation

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseError
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationUpdateModules<T> : ValidationCode<Response<T>> {
    val gson = Gson()

    override fun executeValidation(response: Response<T>) {
        when (response.code()) {
            400 -> {
                val itemError: ResponseError? = try {
                    gson.fromJson(response.errorBody()?.source().toString().replace("[size=85 text=", "").replace("]", """"}"""),
                            ResponseError::class.java)
                } catch (ex: Exception) {
                    null
                }

                itemError?.let {
                    when (it.code) {
                        0 -> throw BackendException("El usuario no pertenece a la iglesia sobre la que se intenta actualizar sus módulos")
                        else -> throw BackendException("El usuario no pertenece a la iglesia sobre la que se intenta actualizar sus módulos")
                    }
                }
            }
        }
    }

}