package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.EventResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST(WebConfig.EVENT)
    fun postUpdateEventAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @GET(WebConfig.EVENT)
    fun getDinerEventAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_PATH)
    fun getDinerEventAsync(@Path("dinerId") dinerId: Int): Deferred<Response<List<DinerResponse>>>
}