package mx.arquidiocesis.eamxcommonutils.retrofit.managercall

import android.content.Context
import android.util.Log
import mx.arquidiocesis.eamxcommonutils.util.repositoryrefreshtoken.ViewModelTokenRefresh
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResultApi
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.BackendException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.ConnectionException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.exception.TokenCodeException
import mx.arquidiocesis.eamxcommonutils.retrofit.model.inter.ValidationCode
import mx.arquidiocesis.eamxcommonutils.util.log
import retrofit2.HttpException
import retrofit2.Response

open class ManagerCall{
    private val MESSAGE_ERROR_GENERAL : String  = "Por el momento no es posible realizar esta operación, intentelo más tarde."
    private val MESSAGE_ERROR_GENERAL_NETWORK : String  = "Ocurrió un error al intentar conectar con nuestros servidores."

    private val TAG = "ManagerCall"

    suspend fun <T : Any> managerCallApi(call: suspend () -> Response<T>, validation : ValidationCode<Response<T>>? = null, context : Context?=null) : ResponseData<T?> {
        val result : ResultApi<T> = safeApiResult(call, validation, context)
        val dataResponse : ResponseData<T?> = ResponseData()
        when(result){
            is ResultApi.Success -> {
                dataResponse.apply {
                    sucess = true
                    data = result.data
                }
            }
            is ResultApi.Error -> {
                dataResponse.exception = result.exception
            }
        }

        return dataResponse
    }

    private suspend fun <T : Any> safeApiResult(call: suspend () -> Response<T>, validation : ValidationCode<Response<T>>? = null, context : Context?=null): ResultApi<T> {
        var exception: Exception? = null
        var data: T? = null
        try {

            val response = call.invoke()

            if (response.code() == 401) {
                exception = TokenCodeException("No fue posible consultar la información, intentelo más tarde.")
                context?.let { it ->
                    val repository = ViewModelTokenRefresh(it)
                    exception?.let { ex ->
                        repository.validErrorToken(ex)
                    }
                }
            }else{
                //Lanza una exepción en caso de que que presentar un error
                validation?.executeValidation(response)
            }

            if (response.isSuccessful) {
                data = response.body()
            }

        } catch (ex: BackendException) {
            "LOG-$TAG BackendException -> ${ex.message}".log()
            exception = Exception(ex.message)
        } catch (ex: ConnectionException) {
            "LOG-$TAG ConnectionException -> ${ex.message}".log()
            exception = Exception(ex.message)
        } catch (ex: HttpException) {
            "LOG-$TAG HttpException -> ${ex.message}".log()
            exception = Exception(MESSAGE_ERROR_GENERAL_NETWORK)
        } catch (ex: Exception) {
            "LOG-$TAG Exception ->  ${ex.message}".log()
            exception = Exception(MESSAGE_ERROR_GENERAL)
        }

        return exception?.let {
            ResultApi.Error(it)
        } ?: kotlin.run {
            ResultApi.Success(data)
        }
    }
}