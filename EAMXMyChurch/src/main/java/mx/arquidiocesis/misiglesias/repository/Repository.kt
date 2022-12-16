package mx.arquidiocesis.misiglesias.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxcommonutils.util.downloadImageUrlToBitArray
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.misiglesias.config.WebConfig.HOST_NEW
import mx.arquidiocesis.misiglesias.model.*
import mx.arquidiocesis.misiglesias.retrofit.ApiInterface
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.eamxmaps.retrofit.validationlayer.ValidationCodes
import mx.arquidiocesis.misiglesias.database.model.ChurchModelLocal
import java.io.File
import java.io.FileOutputStream

class Repository(val context: Context, val database: MeetRoomDataBase) : ManagerCall() {
    //val profileResponse
    val allChurchList = MutableLiveData<MisIgleciasModel?>()
    val suggestionList = MutableLiveData<List<mx.arquidiocesis.misiglesias.model.Location>?>()
    val favResponse = MutableLiveData<Int>()
    val delResponse = MutableLiveData<Int>()
    val iglesiasMapResponse = MutableLiveData<List<IgleciasModel>?>()
    val detalleResponse = MutableLiveData<ChurchDetaillModel>()
    val iglesiasPutResponse = MutableLiveData<Int>()
    val catalogoMassesResponse = MutableLiveData<List<MasseModel>>()
    val catalogoServiciosResponse = MutableLiveData<List<CatServiceModel>>()
    val getIglesiasBusqueda = MutableLiveData<List<IglesiaBusquedaModel>>()
    val priestResponse = MutableLiveData<List<PriestModel>>()
    val errorResponse = MutableLiveData<String>()
    val locationResponse = MutableLiveData<Location>()
    val errorExitScreenResponse = MutableLiveData<String>()

    private val dataBaseLocal by lazy {
        MeetRoomDataBase.getRoomInstance(context).churchDao()
    }

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)

    suspend fun iglesiasList(userId: Int) {
        val userNeedDownloadChurch = eamxcu_preferences.getData(
            EAMXEnumUser.CHURCH_UPDATE_FROM_PROFILE.name,
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
        val localList = if (userNeedDownloadChurch.not()) dataBaseLocal.getAll(userId) else listOf()

        "CHURCHS LOCAL -> $localList".log()

        if (localList.isEmpty()) {
            managerCallApi(
                context = context,
                call = {
                    retrofitInstance
                        .setHost(HOST_NEW)
                        .builder().instance().getFavoritasAsync(userId, "CHURCH").await()
                }, validation = ValidationCodes()

            ).let { response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (response.sucess) {
                        response.data?.let {
                            "CHURCHS INTERNET -> $it".log()
                            saveChurchLocal(it, userId)
                            allChurchList.value = it
                            eamxcu_preferences.saveData(
                                EAMXEnumUser.CHURCH_UPDATE_FROM_PROFILE.name,
                                false
                            )
                        }
                    } else {
                        allChurchList.value = null
                    }
                }
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                allChurchList.value = localList.convertToLocationMyChurch()
            }
        }
    }
    suspend fun suggestions() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getListadoSuggestion().await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    suggestionList.value = response.data
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    private fun saveChurchLocal(churchData: MisIgleciasModel, userId: Int) {
        churchData.assigned?.let { assigned ->
            downloadAssigned(assigned) {
                churchData.locations?.let { locations ->
                    downloadLocation(locations) {
                        "CHURCH LOCAL ASSIGNED -> $churchData".log()
                        saveInDatabase(churchData, userId)
                    }
                } ?: kotlin.run {
                    "CHURCH LOCAL LOCATIONS -> $churchData".log()
                    saveInDatabase(churchData, userId)
                }
            }
        } ?: kotlin.run {
            churchData.locations?.let {
                downloadLocation(it) {

                    "CHURCH LOCAL FAVORITE-> $churchData".log()
                    saveInDatabase(churchData, userId)
                }
            }
        }
    }

    private fun downloadAssigned(assigned: Assigned, listener: () -> Unit) {
        assigned.image_url.downloadImageUrlToBitArray(context) {
            assigned.arrayImage = it
            listener()
        }
    }

    private fun downloadLocation(
        locations: List<mx.arquidiocesis.misiglesias.model.Location>,
        listener: () -> Unit
    ) {
        locations.forEachIndexed { index, item ->
            item.image_url.downloadImageUrlToBitArray(context) {
                item.arrayImage = it
                if (index == (locations.size - 1)) {
                    listener()
                }
            }
        }
    }

    private fun saveInDatabase(churchData: MisIgleciasModel, userId: Int) {
        database.churchDao().addChurchs(churchData.convertToChurchModelLocal(userId))
    }

    suspend fun setFavorita(
        church: ChurchDetaillModel,
        isPrincipal: Boolean,
        userId: Int
    ) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("location_id", church.id)
        jsonObject.addProperty("is_principal", isPrincipal)

        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().setFavoritasAsync(userId, jsonObject).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    saveChurchDetailLocal(church, isPrincipal, userId)
                    favResponse.value = 200
                } else {
                    val churchList = dataBaseLocal.getAll(userId)

                    if (churchList.size < 10) {
                        errorResponse.value = response.exception?.message
                    } else {
                        errorResponse.value = "Ya cuentas el maximo de sedes favoritas registradas"
                    }
                }
            }
        }
    }

    private fun saveChurchDetailLocal(
        church: ChurchDetaillModel,
        isMain: Boolean,
        userId: Int
    ) {

        if (isMain) {
            database.churchDao().deleteChurchMain(userId, true)
        }

        church.image_url?.let { url ->
            church.id?.let { id ->
                database.churchDao().addChurch(
                    ChurchModelLocal(
                        idChurch = id,
                        isChurchMain = isMain,
                        name = church.name ?: "",
                        image_url = url,
                        arrayImage = null,
                        userId = userId
                    )
                )
                "CHURCH SAVE".log()
                /*url.downloadImageUrlToBitArray(context) { buffer ->

                }*/
            }
        } ?: run {
            church.id?.let { id ->
                database.churchDao().addChurch(
                    ChurchModelLocal(
                        idChurch = id,
                        isChurchMain = isMain,
                        name = church.name ?: "",
                        image_url = "",
                        arrayImage = null,
                        userId = userId
                    )
                )
                "CHURCH SAVE".log()
            }
        }
    }

    suspend fun delFavorita(id: Int, idChurch: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().delFavoritasAsync(id, idChurch).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    database.churchDao().deleteChurchFavorite(id, idChurch)
                    delResponse.value = 200
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getiglesiasList() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getListadoMapIglesiasAsync("CHURCH").await()
            }, validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    iglesiasMapResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun obtenerDetalle(id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getDetalleIglesiasAsync(PublicFunctions().getUserId(), id)
                    .await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    detalleResponse.value = response.data!!
                } else {
                    errorExitScreenResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun putIglesias(id: Int, jsonObject: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().addIglesiasRegisterAsync(id, jsonObject).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    iglesiasPutResponse.value = 200
                } else {
                    errorResponse.value =
                        "Para poder guardar la información, te pedimos que llenes todos los campos de horarios y subas por lo menos un horario de misa y una actividad"
                }
            }
        }
    }

    suspend fun getCatalogServicios() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getCatalogServiciosAsync().await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    catalogoServiciosResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getIglesiasBusqueda(churh: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getBuscarIglesiasAsync(churh).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    getIglesiasBusqueda.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getpriests(name: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(HOST_NEW)
                    .builder().instance().getBuscarPriestsAsync(name).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    priestResponse.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    fun imagenInterna(context: Context, uri: String): File {
        val folder =
            File(context.applicationContext.filesDir.toString() + "/images/")
        folder.mkdir()
        return File(folder, "$uri.jpg")
    }

    fun descargarImagen(contexto: Context, listImg: String, id: Int) {

        Glide.with(contexto)
            .asBitmap()
            .load(Uri.parse(listImg))
            .override(300, 200)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    var dest = imagenInterna(contexto, id.toString())
                    try {
                        val out = FileOutputStream(dest)
                        resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }

    fun getLocation(): MutableLiveData<Location> {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationResponse.value = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            // Request location updates
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {
            errorResponse.value = "No fue posible obtener sus datos de geolocalización."
        }

        return locationResponse
    }
}