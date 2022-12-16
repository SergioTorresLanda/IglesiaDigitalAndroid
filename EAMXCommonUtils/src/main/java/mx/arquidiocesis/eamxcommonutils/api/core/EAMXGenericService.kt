package mx.arquidiocesis.eamxcommonutils.api.core

import retrofit2.Call
import retrofit2.http.*

interface EAMXGenericService {

    @POST
    fun serviceResponsePost(@Url url: String, @Body requestModel: Any): Call<Any>

    @POST
    fun serviceResponsePostVoid(@Url url: String, @Body requestModel: Any): Call<Void>

    @GET
    fun serviceResponseGet(@Url url: String, @Body requestModel: Any): Call<Any>

    @PUT
    fun serviceResponsePut(@Url url: String, @Body requestModel: Any): Call<Any>

    @PUT
    fun serviceResponsePutVoid(@Url url: String, @Body requestModel: Any): Call<Void>
}