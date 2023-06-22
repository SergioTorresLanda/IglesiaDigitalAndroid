package mx.arquidiocesis.eamxevent.retrofit

import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxevent.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    //Event_Diner
    @POST(WebConfig.EVENT_DINER)
    fun postCreateEventAsync(@Body event: Event): Deferred<Response<Void>>

    @POST(WebConfig.EVENT_OTHER)
    fun postCreateOtherAsync(
        //@Query("evento") tipoEvento: Int,
        @Body event: OtherEvent
    ): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_DINER_PATH)
    fun putUpdateEventAsync(
        @Path("dinerId") dinerId: Int,
        @Body event: Event,
    ): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_OTHER_UPDATE)
    fun putUpdateOtherAsync(
        @Path("eventId") eventId: Int,
        @Query("evento") tipoEvento: Int,
        @Body event: OtherEvent,
    ): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_DINER)
    fun getDinerEventAsync(): Deferred<Response<List<DinerResponse>>>

    @GET(WebConfig.EVENT_DINER_PATH)
    fun getDinerEventAsync(@Path("dinerId") dinerId: Int): Deferred<Response<List<DinerResponse>>>

    //Event_Pantry
    @POST(WebConfig.EVENT_PANTRY)
    fun postCreateEventPantryAsync(@Body event: Pantry): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_PANTRY_PATH)
    fun putUpdateEventPantryAsync(
        @Path("pantryId") pantryId: Int,
        @Body event: Pantry,
    ): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_PANTRY)
    fun getPantriesEventAsync(): Deferred<Response<List<Pantry>>>

    @GET(WebConfig.EVENT_PANTRY_PATH)
    fun getPantryEventAsync(@Path("pantryId") pantryId: Int): Deferred<Response<List<Pantry>>>

    @GET(WebConfig.EVENT_OTHER_GET)
    fun getOthersEventAsync(): Deferred<Response<List<OtherEvent>>>

    @GET(WebConfig.EVENT_OTHER_ACTORS)
    fun getOthersActorsAsync(): Deferred<Response<List<OtherActor>>>

    @POST(WebConfig.EVENT_OTHER_ACTORS)
    fun postOthersActorsAsync(@Body actor: OtherActor): Deferred<Response<Void>>

    @PUT(WebConfig.UPDATE_OTHER_ACTORS)
    fun putOthersActorsAsync(
        @Path("actorId") actorId : Int,
        @Body actor: OtherActor): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_OTHER_GETDET)
    fun getOtherEventAsync(
        @Path("eventId") eventId: Int,
        @Query("evento") tipoEvento: Int
    ): Deferred<Response<List<OtherEvent>>>

    @POST(WebConfig.EVENT_DONOR)
    fun postCreateDonorAsync(@Body donor: Donor): Deferred<Response<Void>>

    @PUT(WebConfig.EVENT_DONOR_PATH)
    fun putUpdateDonorAsync(
        @Path("donadorId") donadorId: Int,
        @Body donor: Donor,
    ): Deferred<Response<Void>>

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
    fun putUpdateVolunteerAsync(
        @Path("voluntarioId") voluntarioId: Int,
        @Body voluntario: Volunteer,
    ): Deferred<Response<Void>>

    @GET(WebConfig.EVENT_VOLUNTEER)
    fun getVolunteerAsync(): Deferred<Response<List<VolunteerResponse>>>

    @GET(WebConfig.EVENT_VOLUNTEER_PATH)
    fun getVolunteerAsync(@Path("voluntarioId") voluntarioId: Int): Deferred<Response<List<VolunteerResponse>>>

    @GET(WebConfig.GET_TYPE_PARTICIPATION_BY_DINER)
    fun getDonorbyDiner(
        @Path("dinerId") dinerId: Int,
        @Query("participantes") type: String,
    ): Deferred<Response<List<DonorResponse>>>

    @GET(WebConfig.GET_TYPE_PARTICIPATION_BY_DINER)
    fun getVolunteerbyDiner(
        @Path("dinerId") dinerId: Int,
        @Query("participantes") type: String,
    ): Deferred<Response<List<VolunteerResponse>>>

}