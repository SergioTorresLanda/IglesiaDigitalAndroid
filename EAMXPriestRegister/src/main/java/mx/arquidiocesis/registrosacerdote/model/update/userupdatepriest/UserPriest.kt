package mx.arquidiocesis.registrosacerdote.model.update.userupdatepriest

import com.google.gson.annotations.Expose
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel
import mx.arquidiocesis.registrosacerdote.model.catalog.InterestTopic
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseOnlyId
import mx.arquidiocesis.registrosacerdote.model.update.base.BaseUpdate

class UserPriest(
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
    val birthdate: String,
    @Expose
    val ordination_date: String,
    @Expose
    val description: String,
    @Expose
    val position: String,
    @Expose
    val stream: String,
    @Expose
    val congregation: BaseOnlyId?,
    @Expose
    val activities: List<ActivitiesModel>,
): BaseUpdate(id, username, email, first_surname, name, phone_number, second_surname, life_status, interest_topics)