package mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass

import java.lang.Exception

data class ResponseData<T>(
    var sucess : Boolean = false,
    var data: T? = null,
    var exception: Exception? = null
)