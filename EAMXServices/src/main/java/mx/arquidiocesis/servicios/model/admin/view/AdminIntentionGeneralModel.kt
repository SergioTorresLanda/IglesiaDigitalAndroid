package mx.arquidiocesis.servicios.model.admin.view

import android.view.View

data class AdminIntentionGeneralModel(
    val hour : String,
    val nameIntention : String,
    val date : String,
    val speaker : String
){
    val visibleNameInteraction : Int
    get()  {
        return if(nameIntention.isEmpty()) View.GONE else View.VISIBLE
    }
}