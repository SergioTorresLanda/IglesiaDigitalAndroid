package mx.arquidiocesis.sos.repository.model

import java.lang.Exception

data class ResponseData<T>(
    var data: T? = null,
    var exception: Exception? = null
)