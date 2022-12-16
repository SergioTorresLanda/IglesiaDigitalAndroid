package mx.arquidiocesis.registrosacerdote.model.update.userupdatecaselaicconsecratedreligious

import com.google.gson.annotations.Expose
import mx.arquidiocesis.registrosacerdote.model.catalog.InterestTopic
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseOnlyId
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseUpdate

class UserLaicConsecratedReligious (
    id: Int,
    username: String,
    email: String,
    first_surname: String,
    name: String,
    phone_number: String,
    second_surname: String,
    life_status: BaseOnlyId,
    interest_topics: List<InterestTopic>,
    @Expose
    val congregation: BaseOnlyId,
    @Expose
    val pastoral_work: String
): BaseUpdate(id, username, email, first_surname, name, phone_number, second_surname, life_status, interest_topics)