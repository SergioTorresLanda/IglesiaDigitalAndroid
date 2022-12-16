package mx.arquidiocesis.sos.retrofit.validationlayer

import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationCodeSOSpending<T> : ValidationCode<Response<T>> {

    override fun executeValidation(response: Response<T>) {
        if(response.code() == 500){
            throw BackendException("ERROR 500")
        }
    }
}