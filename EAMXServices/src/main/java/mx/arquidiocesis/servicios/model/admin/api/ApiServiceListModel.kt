package mx.arquidiocesis.servicios.model.admin.api

import mx.arquidiocesis.eamxcommonutils.util.transformDateYYYYMMddToDDMonthNameYYYY
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel
import kotlin.collections.ArrayList

class ApiServiceListModel : ArrayList<ApiServiceListModelItem>()

fun ApiServiceListModel.mapToAdminServiceModel(): List<AdminServiceModel> {
    return this.map { item ->
        AdminServiceModel(
            id = item.id,
            service = item.service.name,
            applicant = "${item.devotee.name} ${if (!item.devotee.first_surname.isNullOrEmpty()) item.devotee.first_surname else ""}",
            name = "",
            stateTitle = "",
            state = item.getStatusView(),
            date = item.creation_date.transformDateYYYYMMddToDDMonthNameYYYY(),
        )
    }
}