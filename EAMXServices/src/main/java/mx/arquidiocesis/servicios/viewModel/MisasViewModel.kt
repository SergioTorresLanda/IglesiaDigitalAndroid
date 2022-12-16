package mx.arquidiocesis.servicios.viewModel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.servicios.model.DevoteeModel
import mx.arquidiocesis.servicios.model.LocationModel
import mx.arquidiocesis.servicios.model.ServiceModel
import mx.arquidiocesis.servicios.model.SetServicesModel
import mx.arquidiocesis.servicios.repository.Repository
import org.intellij.lang.annotations.JdkConstants

class MisasViewModel(private val repositoryMeasses: Repository) : ViewModel() {

    val response = repositoryMeasses.misasResponse
    var errorResponse = repositoryMeasses.errorResponse
    val locationResponse = repositoryMeasses.locationResponse
    val postServicesResponse = repositoryMeasses.postServicesResponse

    fun misasList(location: Location) {
        GlobalScope.launch {
            repositoryMeasses.misasList(location)
        }
    }

    fun setService(
        serviceModel: ServiceModel,
        idIglecia: Int,
        family: String,
        description: String,
        zipCode: String,
        colonia:String,
        lon:Double,
        lat:Double,
        justification: String?
        ) {
        val userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        val email = eamxcu_preferences.getData(
            EAMXEnumUser.USER_EMAIL.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val telephone = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PHONE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        var jsonObject: JsonObject = JsonObject()
        if(!justification.isNullOrEmpty()){
            jsonObject.addProperty("explanation", justification)
            jsonObject.addProperty("person_name", family)
        }else{
            jsonObject.addProperty("family_name", family)
        }

        jsonObject.addProperty("email", email)
        jsonObject.addProperty("phone", telephone)

        var address: JsonObject = JsonObject()
        address.addProperty("description", description)
        address.addProperty("zipcode", zipCode)
        address.addProperty("neighborhood", colonia)
        address.addProperty("longitude", lon)
        address.addProperty("latitude", lat)

        jsonObject.add("address", address)
        jsonObject.addProperty("location_id", idIglecia)
        jsonObject.addProperty("service_id", serviceModel.type!!.id)


        GlobalScope.launch {
            repositoryMeasses.setService(jsonObject, userId)
        }
    }
    fun getLocation() {
        repositoryMeasses.getLocation()
    }

}