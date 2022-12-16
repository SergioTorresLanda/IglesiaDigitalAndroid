package mx.arquidiocesis.servicios.retrofit.validationlayer

import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationRejected<T> : ValidationCode<Response<T>> {

    override fun executeValidation(response: Response<T>) {
        if(response.code() != 500){
            throw BackendException("Por el momento no es posible hacer esta operación, intentelo mas tarde.")
        }
    }
}