package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.Event
import mx.arquidiocesis.eamxevent.model.EventResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    //Event_Diner
    @POST(WebConfig.EVENT_DINER)
    fun postCreateEventAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @PUT(WebConfig.EVENT_DINER_PATH)
    fun putUpdateEventAsync(@Path("dinerId") dinerId: Int, @Body event: Event) : Deferred<Response<EventResponse>>

    @GET(WebConfig.EVENT_DINER)
    fun getDinerEventAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_DINER_PATH)
    fun getDinerEventAsync(@Path("dinerId") dinerId: Int): Deferred<Response<List<DinerResponse>>>

    //Event_Donor

    @POST(WebConfig.EVENT_DONOR)
    fun postCreateDonorAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @PUT(WebConfig.EVENT_DONOR_PATH)
    fun putUpdateDonorAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @GET(WebConfig.EVENT_DONOR)
    fun getDonorAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_DONOR_HISTORY_PATH)
    fun getDonorHistoryAsync(@Path("donorId") dinerId: Int): Deferred<Response<List<DinerResponse>>>

    //Event_Volunteer

    @POST(WebConfig.EVENT_VOLUNTEER)
    fun postCreateVolunteerAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @PUT(WebConfig.EVENT_VOLUNTEER_PATH)
    fun putUpdateVolunteerAsync(@Body event: Event) : Deferred<Response<EventResponse>>

    @GET(WebConfig.EVENT_VOLUNTEER)
    fun getVolunteerAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_DINER_PATH)
    fun getVolunteerHistoryAsync(@Path("dinerId") dinerId: Int): Deferred<Response<List<DinerResponse>>>

}