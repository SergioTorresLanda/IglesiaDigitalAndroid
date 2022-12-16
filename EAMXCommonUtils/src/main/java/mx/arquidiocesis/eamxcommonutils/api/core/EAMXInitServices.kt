package mx.arquidiocesis.eamxcommonutils.api.core

import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXRestEngine

class EAMXInitServices<M, R>(baseUrl: String? = null ) {

    var userService: EAMXGenericService = EAMXRestEngine.getRestEngine(baseUrl).create(EAMXGenericService::class.java)

    fun postExecuteService(mRequest: M, endPoint: String) =
        userService.serviceResponsePost(endPoint, (mRequest as Any)) as R

    fun postExecuteServiceVoid(mRequest: M, endPoint: String) =
        userService.serviceResponsePostVoid(endPoint, (mRequest as Any))

    fun getExecuteService(mRequest: M, endPoint: String) =
        userService.serviceResponseGet(endPoint, (mRequest as Any)) as R

    fun putExecuteService(mRequest: M, endPoint: String) =
        userService.serviceResponsePut(endPoint, (mRequest as Any)) as R

    fun putExecuteServiceVoid(mRequest: M, endPoint: String) =
        userService.serviceResponsePutVoid(endPoint, (mRequest as Any))
}