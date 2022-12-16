package mx.arquidiocesis.registrosacerdote.retrofit.validation

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationCollaborators<T> : ValidationCode<Response<T>> {
    val gson = Gson()

    override fun executeValidation(response: Response<T>) {
        when (response.code()) {
            200 -> {}
            400 -> throw BackendException("El colaborador no pertenece a la sede del administrador o no presta servicios")
            else -> throw BackendException("No fue posible recuperar la información del usuario, intentalo mas tarde")
        }
    }

}