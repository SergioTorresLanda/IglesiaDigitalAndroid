package mx.arquidiocesis.registrosacerdote.retrofit.validation

import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationPrefileGeneral<T> : ValidationCode<Response<T>> {

    override fun executeValidation(response: Response<T>) {
        if(response.code() != 201 && response.code() != 200){
            throw BackendException("Por el momento no es posible hacer esta operación, intentelo mas tarde.")
        }
    }
}