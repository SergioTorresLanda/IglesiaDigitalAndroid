package mx.arquidiocesis.eamxregistromodule.ui.confirm.model

data class ResponseMetadata(
        val HTTPHeaders: HTTPHeaders,
        val HTTPStatusCode: Int,
        val RequestId: String,
        val RetryAttempts: Int
)