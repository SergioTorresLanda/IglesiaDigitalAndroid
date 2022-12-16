package mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass

import java.lang.Exception

sealed class ResultApi<out T: Any>{
    data class Success<out T : Any>(val data: T?) : ResultApi<T>()
    data class Error(val exception: Exception) : ResultApi<Nothing>()
}
