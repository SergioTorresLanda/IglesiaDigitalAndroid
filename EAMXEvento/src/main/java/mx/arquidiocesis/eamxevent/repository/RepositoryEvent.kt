package mx.arquidiocesis.eamxevent.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.retrofit.model.dataclass.ResponseData
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent
import mx.arquidiocesis.eamxevent.model.*
import retrofit2.Call
import mx.arquidiocesis.eamxevent.retrofit.ApiInterface
import mx.arquidiocesis.eamxevent.retrofit.Validation
import mx.arquidiocesis.eamxevent.retrofit.WebConfig.HOST

class RepositoryEvent(val context: Context) : ManagerCall() {

    val errorResponse = MutableLiveData<String>()
    val saveResponse = MutableLiveData<String>()

    //GET Event
    val allDiner = SingleLiveEvent<List<DinerResponse>>()
    val allPantry = SingleLiveEvent<List<Pantry>>()
    val allOther = SingleLiveEvent<List<OtherEvent>>()
    val allDonor = SingleLiveEvent<List<DonorResponse>>()
    val allVolunteer = SingleLiveEvent<List<VolunteerResponse>>()
    val allGuest = SingleLiveEvent<List<GuestModel>>()

    private var retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setClass(ApiInterface::class.java)

    private val retrofitInstances = RetrofitApp.Build<ApiInterface>()
        .setHost(HOST)
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)
        .builder().instance()

    fun callServiceEvent(
        requestModel: Event,
        observer: Observer<EAMXGenericResponse<EventResponse, String, Event>>
    ) {
        val initService: EAMXInitServices<Event, Call<EventResponse>> =
            EAMXInitServices()
        EAMXValidResponse(observer, requestModel, EventResponse::class).validationMethod(
            initService.postExecuteService(requestModel, AppMyConstants.CREATE_DINER)
        )
    }

    suspend fun saveEventDiner(event: Event) {
        managerCallApi(
            context = context, call = {
                retrofitInstance.setHost(HOST).builder().instance().postCreateEventAsync(event)
                    .await()
            }, validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha dado de alta el comedor correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La creación del comedor no se pudo realizar."
                }
            }
        }
    }

    suspend fun saveEventOther(event: OtherEvent) {
        managerCallApi(
            context = context, call = {
                retrofitInstance.setHost(HOST).builder().instance().postCreateOtherAsync(event)
                    .await()
            }, validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha dado de alta la actividad correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "No se ha dado de alta la actividad correctamente."
                }
            }
        }
    }

    suspend fun UpdateEventDiner(dinerId: Int, event: Event) {
        managerCallApi(
            context = context, call = {
                retrofitInstance.setHost(HOST).builder().instance().putUpdateEventAsync(dinerId, event).await()
            }, validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha actualizado el comedor correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La actualización del comedor no se pudo realizar."
                }
            }
        }
    }

    suspend fun updateEventOther(otherId: Int, event: OtherEvent) {
        managerCallApi(
            context = context, call = {
                retrofitInstance.setHost(HOST).builder().instance().putUpdateOtherAsync(otherId, event).await()
            }, validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha actualizado la actividad correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "No se ha actualizado la actividad correctamente."
                }
            }
        }
    }

    suspend fun getAllDiner(dinerId: Int): ResponseData<List<DinerResponse>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    if (dinerId == 0)
                        getDinerEventAsync().await()
                    else
                        getDinerEventAsync(dinerId).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun saveEventPantry(event: Pantry) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance().postCreateEventPantryAsync(event)
                    .await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha dado de alta la actividad de despensas correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La creación de la actividad de despensa no se pudo realizar."
                }
            }
        }
    }

    suspend fun UpdateEventPantry(pantryId: Int, event: Pantry) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance()
                    .putUpdateEventPantryAsync(pantryId, event).await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha actualizado la despensa correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La actualización de la despensa no se pudo realizar."
                }
            }
        }
    }

    suspend fun getAllPantry(pantryId: Int): ResponseData<List<Pantry>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    if (pantryId == 0)
                        getPantriesEventAsync().await()
                    else
                        getPantryEventAsync(pantryId).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun getAllOther(otherId: Int): ResponseData<List<OtherEvent>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    if (otherId == 0)
                        getOthersEventAsync().await()
                    else
                        getOtherEventAsync(otherId).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun saveDonor(donor: Donor) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance().postCreateDonorAsync(donor)
                    .await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha dado de alta el donador correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La creación del donador no se pudo realizar."
                }
            }
        }
    }

    suspend fun UpdateDonor(donorId: Int, donor: Donor) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance()
                    .putUpdateDonorAsync(donorId, donor).await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha actualizado el donador correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La actualización del donador no se pudo realizar."
                }
            }
        }
    }

    suspend fun getAllDonor(donorId: Int): ResponseData<List<DonorResponse>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    if (donorId == 0)
                        getDonorAsync().await()
                    else
                        getDonorAsync(donorId).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun getDonorbyDiner(dinerId: Int, type: String): ResponseData<List<DonorResponse>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    getDonorbyDiner(dinerId, type).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun saveVolunteer(volunteer: Volunteer) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance().postCreateVolunteerAsync(volunteer)
                    .await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha dado de alta el voluntario correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La creación del voluntario no se pudo realizar."
                }
            }
        }
    }

    suspend fun UpdateVolunteer(voluntarioId: Int, voluntario: Volunteer) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(HOST).builder().instance()
                    .putUpdateVolunteerAsync(voluntarioId, voluntario).await()
            },
            validation = Validation()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveResponse.value = "Se ha actualizado el voluntario correctamente."
                } else {
                    errorResponse.value = response.exception?.message
                        ?: "La actualización del voluntario no se pudo realizar."
                }
            }
        }
    }

    suspend fun getAllVolunteer(voluntarioId: Int): ResponseData<List<VolunteerResponse>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    if (voluntarioId == 0)
                        getVolunteerAsync().await()
                    else
                        getVolunteerAsync(voluntarioId).await()
                }
            },
            validation = Validation()
        )
    }

    suspend fun getVolunteerbyDiner(dinerId: Int, type: String): ResponseData<List<VolunteerResponse>?> {
        return managerCallApi(
            context = context,
            call = {
                retrofitInstances.run {
                    getVolunteerbyDiner(dinerId, type).await()
                }
            },
            validation = Validation()
        )
    }
}