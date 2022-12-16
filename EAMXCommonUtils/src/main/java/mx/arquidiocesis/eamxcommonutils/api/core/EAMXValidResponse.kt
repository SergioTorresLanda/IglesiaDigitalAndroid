package mx.arquidiocesis.eamxcommonutils.api.core

import android.util.MalformedJsonException
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.arquidiocesis.eamxcommonutils.api.core.errorresponse.EAMXErrorResponseEnum
import mx.arquidiocesis.eamxcommonutils.api.core.errorresponse.EAMXErrorResponseGeneric
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import kotlin.reflect.KClass

class EAMXValidResponse<R, G>(
    val observer: Observer<EAMXGenericResponse<R, String, G>>, val requestData: G,
    val vkClass: KClass<*>
) {
    fun validationMethod(result: Call<R>) {

        result.enqueue(object : Callback<R> {
            override fun onFailure(call: Call<R>, t: Throwable) {
                if (t is ConnectException) {
                    observer.onChanged(
                        EAMXGenericResponse(
                            EAMXStatusRequestEnum.FAILURE,
                            null,
                            EAMXErrorResponseEnum.CONEXION_ERROR.messageError,
                            requestData
                        )
                    )
                } else if (t is MalformedJsonException) {
                    observer.onChanged(
                        EAMXGenericResponse(
                            EAMXStatusRequestEnum.FAILURE,
                            null,
                            EAMXErrorResponseEnum.MALFORMED_JSON.messageError,
                            requestData
                        )
                    )
                } else {
                    observer.onChanged(
                        EAMXGenericResponse(
                            EAMXStatusRequestEnum.FAILURE,
                            null,
                            EAMXErrorResponseEnum.ERROR_DB.messageError,
                            requestData
                        )
                    )
                }
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                when (response.code()) {
                    200 -> {
                        if (response.body() != null) {
                            val gson = Gson()
                            val jsonObject = gson.toJsonTree(response.body()).asJsonObject
                            val myResponse =
                                Gson().fromJson(jsonObject, vkClass.javaObjectType) as R
                            observer.onChanged(
                                EAMXGenericResponse(
                                    EAMXStatusRequestEnum.SUCCESS,
                                    myResponse, requestData = requestData
                                )
                            )
                        } else {
                            val myResponse = "Success." as R
                            observer.onChanged(
                                EAMXGenericResponse(
                                    EAMXStatusRequestEnum.SUCCESS,
                                    myResponse, requestData = requestData
                                )
                            )
                        }

                    }
                    403 -> {
                        observer.onChanged(
                            EAMXGenericResponse(
                                EAMXStatusRequestEnum.FAILURE,
                                null,
                                validateTypeError(response.code()),
                                requestData
                            )
                        )
                    }
                    else -> {
                        if (response.errorBody() != null) {
                            val type = object : TypeToken<EAMXErrorResponseGeneric>() {}.type
                            var errorResponseGeneric: EAMXErrorResponseGeneric
                            response.errorBody()?.let {
                                try {
                                    errorResponseGeneric = Gson().fromJson(it.charStream(), type)
                                } catch (exception: Exception) {
                                    errorResponseGeneric =
                                        EAMXErrorResponseGeneric(
                                            response.code(),
                                            EAMXErrorResponseEnum.ERROR_DB.messageError
                                        )
                                }
                                var mensaje = validateTypeError(errorResponseGeneric.code)
                                if (!errorResponseGeneric.error.isNullOrEmpty()) {
                                    mensaje = errorResponseGeneric.error
                                }
                                observer.onChanged(
                                    EAMXGenericResponse(
                                        EAMXStatusRequestEnum.FAILURE,
                                        null,
                                        mensaje,
                                        requestData
                                    )
                                )
                            }
                        } else {
                            observer.onChanged(
                                EAMXGenericResponse(
                                    EAMXStatusRequestEnum.FAILURE,
                                    null,
                                    validateTypeError(EAMXErrorResponseEnum.ERROR_DB.errorCode),
                                    requestData
                                )
                            )
                        }
                    }
                }
            }
        })
    }

    fun validateTypeError(error: Int) = when (error) {
        EAMXErrorResponseEnum.USER_NOT_EXIST_ERROR.errorCode -> EAMXErrorResponseEnum.USER_NOT_EXIST_ERROR.messageError
        EAMXErrorResponseEnum.INVALID_CONFIRM_CODE.errorCode -> EAMXErrorResponseEnum.INVALID_CONFIRM_CODE.messageError
        EAMXErrorResponseEnum.USER_ALREADY_CONFIRMED.errorCode -> EAMXErrorResponseEnum.USER_ALREADY_CONFIRMED.messageError
        EAMXErrorResponseEnum.USER_NAME_PASSWORD_INCORRECT.errorCode -> EAMXErrorResponseEnum.USER_NAME_PASSWORD_INCORRECT.messageError
        EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.errorCode -> EAMXErrorResponseEnum.USER_IS_NOT_CONFIRMED.messageError
        EAMXErrorResponseEnum.ERROR_PASSWORD_FORMAT.errorCode -> EAMXErrorResponseEnum.ERROR_PASSWORD_FORMAT.messageError
        EAMXErrorResponseEnum.SEND_SMS_LIMITED_EXCEEDED.errorCode -> EAMXErrorResponseEnum.SEND_SMS_LIMITED_EXCEEDED.messageError
        else -> EAMXErrorResponseEnum.ERROR_DB.messageError
    }
}