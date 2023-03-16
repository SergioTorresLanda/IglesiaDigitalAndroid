package mx.arquidiocesis.eamxevent.api

import android.app.DownloadManager.Request
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXGenericService
import mx.arquidiocesis.eamxcommonutils.api.core.request.EAMXRestEngine

class InitServices<M, R>(baseUrl: String? = null ) {

    var userService: EAMXGenericService = RequestApi.getRequestApi(baseUrl).create(
        EAMXGenericService::class.java)

    fun postExecuteService(mRequest: M, endPoint: String) =
        userService.serviceResponsePost(endPoint, (mRequest as Any)) as R

}