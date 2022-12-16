

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import mx.arquidiocesis.eamxpagos.model.BillingModel

class DonacionesViewModel(private val repositoryDonation: RepositoryDonation) : ViewModel() {

    var postResponse = repositoryDonation.postResponse
    val zipCodeResponse = repositoryDonation.zipCodeResponse
    val getResponse = repositoryDonation.getBillingResponse

    var counterMinutes: MutableLiveData<String> = MutableLiveData()
    var counterSeconds: MutableLiveData<String> = MutableLiveData()
    var launchMessageCounter: MutableLiveData<String> = MutableLiveData()
    var succesResponse: MutableLiveData<String> = MutableLiveData()

    var errorResponse = repositoryDonation.errorResponse


    fun setPost(
        model: BillingModel
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("business_name", model.business_Name)
        jsonObject.addProperty("rfc", model.rfc?.uppercase())
        jsonObject.addProperty("address", model.address)
        jsonObject.addProperty("neighborhood", model.neighborhood)
        jsonObject.addProperty("zipcode", model.zipcode)
        jsonObject.addProperty("municipality", model.municipality)
        jsonObject.addProperty("email", model.email)
        jsonObject.addProperty("automatic_invoicing", model.automatic_invoicing)
        GlobalScope.launch {
            repositoryDonation.postBilling(jsonObject)
        }
    }

    fun updateBilling(
        id: Int,
        model: BillingModel
    ) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("business_name", model.business_Name)
        jsonObject.addProperty("rfc", model.rfc)
        jsonObject.addProperty("address", model.address)
        jsonObject.addProperty("neighborhood", model.neighborhood)
        jsonObject.addProperty("zipcode", model.zipcode)
        jsonObject.addProperty("municipality", model.municipality)
        jsonObject.addProperty("email", model.email)
        jsonObject.addProperty("automatic_invoicing", model.automatic_invoicing)
        GlobalScope.launch {
            repositoryDonation.updateBilling(id, jsonObject)
        }
    }

    fun delBilling(id: Int) {
        GlobalScope.launch {
            repositoryDonation.delBilling(id)
        }
    }

    fun getBilling() {
        GlobalScope.launch {
            repositoryDonation.getBilling()
        }
    }

    fun getZipCode(code: String) {
        GlobalScope.launch {
            repositoryDonation.getZipCode(code)
        }
    }

    fun initCounter(): Job {
        var minutes = 4
        var seconds = 59

        counterMinutes.value = "0$minutes"
        counterSeconds.value = seconds.toString()
        val globalScope = GlobalScope.launch(Dispatchers.IO) {
            while (minutes >= 0) {
                Thread.sleep(1000)
                if (seconds == 0) {
                    if (minutes == 0) {
                        withContext(Dispatchers.Main) {
                            launchMessageCounter.value = "salie"
                        }
                        break
                    } else {
                        minutes--
                        val minuteString = if (minutes < 10) {
                            "0$minutes"
                        } else {
                            minutes.toString()
                        }
                        seconds = 59
                        withContext(Dispatchers.Main) {
                            counterMinutes.value = minuteString
                            counterSeconds.value = seconds.toString()
                        }
                    }
                } else {
                    seconds--
                    val secondString = if (seconds < 10) {
                        "0$seconds"
                    } else {
                        seconds.toString()
                    }
                    withContext(Dispatchers.Main) {
                        counterSeconds.value = secondString
                    }
                }
            }
        }
        return globalScope
    }
}