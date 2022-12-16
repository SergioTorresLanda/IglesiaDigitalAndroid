package mx.arquidiocesis.servicios.model.admin.api

import android.util.Log
import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionDetailModel
import mx.arquidiocesis.servicios.model.IntentionDetail
import mx.arquidiocesis.servicios.model.DedicatedTo
import java.util.Objects

data class IntentionsDetailApiModel(
    val intentions: List<ApiIntentionDetailItem>?= null,
    val location: String?= null,
    val mass_date: String?= null,
    val mass_schedule: String?= null
)

fun IntentionsDetailApiModel.mapToAdminIntentionDetailModel() : AdminIntentionDetailModel {
    return AdminIntentionDetailModel(
        churchName = this.location ?: "",
        date = this.mass_date?: "",
        hour =  this.mass_schedule?: "",
        list = this.intentions?.let{
            it.map { item ->
                IntentionDetail(reason = (item.intention?: "")+":", names = item.dedicated_to?.map{ item ->
                    item.to+" \nde parte de "+ item.from + "."
                }
                    ?: "" as List<String>)
            }
        } ?: listOf()
    )
}