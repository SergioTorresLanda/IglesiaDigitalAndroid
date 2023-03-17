package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.EventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @POST(WebConfig.EVENT)
    fun postUpdateEventAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @GET(WebConfig.EVENT)
    fun getDinerEventAsync(): Deferred<Response<DinerResponse>>
}