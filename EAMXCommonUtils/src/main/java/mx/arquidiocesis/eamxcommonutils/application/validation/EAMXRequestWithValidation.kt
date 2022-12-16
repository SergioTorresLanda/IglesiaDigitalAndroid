package mx.arquidiocesis.eamxcommonutils.application.validation

data class EAMXRequestWithValidation<T>(val request: T, val errorMessage: String, val statusValidation: EAMXStatusValidation)