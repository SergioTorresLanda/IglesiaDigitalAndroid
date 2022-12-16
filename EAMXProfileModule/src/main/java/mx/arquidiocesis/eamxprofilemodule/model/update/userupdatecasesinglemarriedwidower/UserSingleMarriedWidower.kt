package mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecasesinglemarriedwidower

import com.google.gson.annotations.Expose
import mx.arquidiocesis.eamxprofilemodule.model.update.base.BaseUpdate
import mx.arquidiocesis.eamxprofilemodule.model.update.base.InterestTopic
import mx.arquidiocesis.eamxprofilemodule.model.update.base.LifeStatus

class UserSingleMarriedWidower (
    id: Int,
    username: String,
    email: String,
    first_surname: String,
    name: String,
    phone_number: String,
    second_surname: String,
    life_status: LifeStatus,
    interest_topics: List<InterestTopic>,
    @Expose
    val services_provided: List<ServicesProvided>
): BaseUpdate(id, username, email, first_surname, name, phone_number, second_surname, life_status, interest_topics)