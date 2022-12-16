package mx.arquidiocesis.servicios.model.admin.view

import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel

data class AdminHistoryModel(
    val date : String,
    val list : List<AdminServiceModel>
)