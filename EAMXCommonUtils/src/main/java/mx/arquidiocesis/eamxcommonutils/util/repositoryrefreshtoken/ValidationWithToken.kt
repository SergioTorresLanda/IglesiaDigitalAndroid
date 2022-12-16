package mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken

import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.TokenCodeException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import retrofit2.Response

class ValidationWithToken <T> constructor(val codesToValidate : List<Int> ?= null) : ValidationCode<Response<T>> {
    override fun executeValidation(response: Response<T>) {
        codesToValidate?.let {
            if(codesToValidate.any { code -> code != response.code()}  ){
                throw BackendException("No fue posible consultar la información, intentelo más tarde.")
            }else if(response.code() == 401){
                throw TokenCodeException("No fue posible consultar la información, intentelo más tarde.")
            }
        } ?: kotlin.run {
            if(response.code() == 401){
                throw TokenCodeException("No fue posible consultar la información, intentelo más tarde.")
            }
        }
    }
}