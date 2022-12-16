package mx.arquidiocesis.eamxcommonutils.api.core.response

import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum

data class EAMXGenericResponse<S, E, Any>(val statusRequest: EAMXStatusRequestEnum = EAMXStatusRequestEnum.NONE,
                                          val successData: S? = null,
                                          val errorData: E? = null,
                                          val requestData: Any)