package mx.arquidiocesis.sos.repository

import android.content.Context
import android.graphics.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.sos.config.WebConfig
import mx.arquidiocesis.sos.model.*
import mx.arquidiocesis.sos.retrofit.ApiInterface
import mx.arquidiocesis.sos.retrofit.validationlayer.ValidationCodeGeneral
import mx.arquidiocesis.sos.retrofit.validationlayer.ValidationCodeSOSpending
import mx.arquidiocesis.sos.retrofit.validationlayer.ValidationCodes
import java.io.File
import java.io.FileOutputStream


class SOSRepository(val context: Context) : ManagerCall() {
    val priestServices = MutableLiveData<List<ServicesPriestModel>>()
    val servicioPendiente = MutableLiveData<RespuestaModel>()
    private val serviceList = MutableLiveData<List<Service>>()
    val errorResponse = MutableLiveData<String>()
    val errorResponseExit = MutableLiveData<String>()
    val actualizarEstatus = MutableLiveData<Int>()
    val getServiceDetalle = MutableLiveData<ServiceDetalleModel>()
    val successRegistrySOS = MutableLiveData<ResponseSOSModel>()
    val locationResponse = MutableLiveData<Location>()
    val locationsSOS = MutableLiveData<List<LocationSOSModel>>()
    val priestList = MutableLiveData<List<ServiceBoxModel>>()
    val imagen = MutableLiveData<File>()

    fun getLiveData(): LiveData<List<Service>> = serviceList

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)

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

    suspend fun getLocationsSOS(latitude: Double, longitude: Double) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .getLocationsSOSAsync(
                        "SOS",
                        latitude,
                        longitude
                    ).await()
            }, validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && !response.data.isNullOrEmpty()) {
                    locationsSOS.value = response.data!!
                } else {
                    errorResponse.value =
                        "No existen iglesias cercanas a la ubicación proporcionada "
                }
            }
        }
    }

    suspend fun setServices(userId: Int, serviceId: Int, status: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance().setStatusAsync(
                    userId,
                    serviceId,
                    StatusModel(status)
                ).await()
            },
            validation = ValidationCodeGeneral()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    actualizarEstatus.value = 200
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun setServicesProgres(userId: Int, serviceId: Int, body: JsonObject, tipo: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance().setStatusProgress(
                    userId,
                    serviceId,
                    body
                ).await()
            },
            validation = ValidationCodeGeneral()

        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && response.data != null) {
                    actualizarEstatus.value = tipo
                } else if (response.exception != null) {
                    errorResponse.value = response.exception?.message
                } else {
                    errorResponse.value = ""
                }
            }
        }
    }

    suspend fun getServices(userId: Int, serviceId: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .getStatusAsync(userId, serviceId).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && response.data != null) {
                    getServiceDetalle.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getPriestServices(id: Int, status: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .getListServicesAsync(
                        id,
                        status,
                        "SOS"
                    ).await()
            },validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && !response.data.isNullOrEmpty()) {
                    priestServices.value = response.data!!
                } else {
                    if (response.exception != null) {
                        errorResponse.value = response.exception?.message
                    } else {
                        errorResponse.value = "No tienes servicios pendientes"
                    }
                }
            }
        }
    }


    suspend fun postRegistrySOS(userId: Int, body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .postRegistrySOSAsync(userId, body).await()
            },
            validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    successRegistrySOS.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getTypeServicesSOS() {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .getServicesListAsync("SOS").await()
            },
            validation = ValidationCodeGeneral()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && !response.data.isNullOrEmpty()) {
                    serviceList.value = response.data!!
                } else {
                    errorResponseExit.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getSOSPendiente(id: Int, role: String, idService: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).builder().instance()
                    .getPendiente(
                        id,
                        role,
                        idService
                    ).await()
            },
            validation = ValidationCodeSOSpending()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    servicioPendiente.value = response.data!!
                } else {
                    if (response.exception!!.message == "ERROR 500") {
                        servicioPendiente.value = RespuestaModel(0, "")
                    } else {
                        errorResponse.value = response.exception?.message
                    }
                }
            }
        }
    }

    suspend fun getPrientsList(id: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOSTAPI).setUseToken(true)
                    .builder().instance()
                    .getSacerdote(id, "SOS").await()
            }, validation = ValidationCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    if (response.data != null) {
                        priestList.value = response.data!!
                    } else {
                        errorResponse.value = response.exception?.message
                    }


                } else {

                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    fun createImage(serviceDetalleModel: ServiceDetalleModel, estado: String) {
        val w: Int = 400
        val h: Int = 300

        val conf = Bitmap.Config.ARGB_8888 // see other conf types

        val bmp = Bitmap.createBitmap(w, h, conf) // this creates a MUTABLE bitmap

        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        val textPaint =
            Paint().apply {
                isAntiAlias = true
                color = Color.BLACK
                style = Paint.Style.FILL
                textSize = 20f
            }
        val titlePaint =
            Paint().apply {
                isAntiAlias = true
                color = Color.BLUE
                style = Paint.Style.FILL_AND_STROKE
                textSize = 20f
            }
        var x = 10f
        var y = 20f
        canvas.drawText("Solicitante", x, y, titlePaint)
        canvas.drawText(serviceDetalleModel.devotee.name, x, y * 2, textPaint)
        canvas.drawText("Dirección", x, y * 4, titlePaint)
        canvas.drawText(serviceDetalleModel.address.description, x, y * 5, textPaint)
        canvas.drawText("Telefono", x, y * 7, titlePaint)
        canvas.drawText(serviceDetalleModel.devotee.phone, x, y * 8, textPaint)
        canvas.drawText("Estado de solicitud", x, y * 10, titlePaint)
        canvas.drawText(estado, x, y * 11, textPaint)
        canvas.save()
        val folder =
            File(context.applicationContext.filesDir.toString() + "/images/")
        folder.mkdir()
        var dest = File(folder, "SOSDetalle${serviceDetalleModel.id}.jpg")
        try {
            val out = FileOutputStream(dest)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        imagen.value = dest
    }

}