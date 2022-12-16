package mx.arquidiocesis.eamxdonaciones.congig

import com.example.eamxdonaciones.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp


object WebConfig {
    val HOST_NEW = ConstansApp.hostEncuentro()
    const val BILLING = "me/tax-data"
    const val BILLINGUPDATE = "me/tax-data/{tax_data_id}"
    const val GET_ZIP_CODE= "catalog/neighborhoods"
    val URLDONATION = ConstansApp.urlDonaciones()
}