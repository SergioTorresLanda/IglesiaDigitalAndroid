package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    //Event_Diner
    @POST(WebConfig.EVENT_DINER)
    fun postCreateEventAsync(@Body event: Event): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_DINER_PATH)
    fun putUpdateEventAsync(
        @Path("dinerId") dinerId: Int,
        @Body event: Event
    ): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_DINER)
    fun getDinerEventAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_DINER_PATH)
    fun getDinerEventAsync(@Path("dinerId") dinerId: Int): Deferred<Response<List<DinerResponse>>>

    //Event_Donor

    @POST(WebConfig.EVENT_DONOR)
    fun postCreateDonorAsync(@Body donor: Donor): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_DONOR_PATH)
    fun putUpdateDonorAsync(@Path("donadorId") donadorId: Int, @Body donor: Donor): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_DONOR)
    fun getDonorAsync(): Deferred<Response<List<DonorResponse>>>

    @GET(WebConfig.EVENT_DONOR_PATH)
    fun getDonorAsync(@Path("donadorId") donadorId: Int): Deferred<Response<List<DonorResponse>>>

    @GET(WebConfig.EVENT_DONOR_HISTORY_PATH)
    fun getDonorHistoryAsync(@Path("donadorId") donadorId: Int): Deferred<Response<List<DonorResponse>>>

    //Event_Volunteer

    @POST(WebConfig.EVENT_VOLUNTEER)
    fun postCreateVolunteerAsync(@Body volunteer: Volunteer): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_VOLUNTEER_PATH)
    fun putUpdateVolunteerAsync(@Path("voluntarioId") voluntarioId: Int, @Body voluntario: Volunteer): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_VOLUNTEER)
    fun getVolunteerAsync(): Deferred<Response<List<VolunteerResponse>>>

    @GET(WebConfig.EVENT_VOLUNTEER_PATH)
    fun getVolunteerAsync(@Path("voluntarioId") voluntarioId: Int): Deferred<Response<List<VolunteerResponse>>>

    @GET(WebConfig.GET_TYPE_PARTICIPATION_BY_DINER)
    fun getDonorbyDiner(@Path("dinerId") dinerId: Int, @Query("participantes") type: String): Deferred<Response<List<DonorResponse>>>

    @GET(WebConfig.GET_TYPE_PARTICIPATION_BY_DINER)
    fun getVolunteerbyDiner(@Path("dinerId") dinerId: Int, @Query("participantes") type: String): Deferred<Response<List<VolunteerResponse>>>

}