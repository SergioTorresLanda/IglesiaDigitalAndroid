package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.EventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST(WebConfig.EVENT)
    fun postUpdateEventAsync(@Body event: Event) : Deferred<Response<EventResponse>>
}