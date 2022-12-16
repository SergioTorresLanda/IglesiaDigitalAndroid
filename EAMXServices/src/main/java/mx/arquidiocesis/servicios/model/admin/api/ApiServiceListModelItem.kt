package mx.arquidiocesis.servicios.model.admin.api
import mx.arquidiocesis.servicios.model.admin.view.StatusService.PENDING_CONFIRMATION
import mx.arquidiocesis.servicios.model.admin.view.StatusService.ACCEPTED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.REJECTED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.COMPLETED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.CANCELLED
import mx.arquidiocesis.servicios.model.admin.view.StatusService.COMPLETED_WITH_WARNING


data class ApiServiceListModelItem(
    val creation_date: String,
    val devotee: ApiDevoteeModel,
    val id: Int,
    val modification_date: String,
    val service: ApiServiceXModel,
    val status: String
){
    fun getStatusView() : String {
        return when(status){
            PENDING_CONFIRMATION -> "Por confirmar"
            ACCEPTED -> "Aceptada"
            REJECTED -> "Rechazada"
            COMPLETED -> "Concluida"
            CANCELLED -> "Cancelada"
            COMPLETED_WITH_WARNING -> "Concluida con alerta"
            else ->  "Sin status"
        }
    }
}