package mx.arquidiocesis.eamxcommonutils.api.core.errorresponse

data class EAMXErrorResponseModel(val errorType: EAMXErrorResponseEnum,
                                  val errorResponse: String)