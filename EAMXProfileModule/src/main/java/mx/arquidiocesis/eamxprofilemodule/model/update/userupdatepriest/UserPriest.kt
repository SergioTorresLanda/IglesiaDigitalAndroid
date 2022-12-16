package mx.arquidiocesis.eamxprofilemodule.model.update.userupdatepriest

import com.google.gson.annotations.Expose
import mx.arquidiocesis.eamxprofilemodule.model.update.base.*

class UserPriest (
        id: Int,
        email: String,
        username: String,
        first_surname: String,
        name: String,
        phone_number: String,
        second_surname: String,
        life_status: LifeStatus,
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
    val congregation: Congregation,
        @Expose
    val activities: List<ActivityModel>,
): BaseUpdate(id, username, email, first_surname, name, phone_number, second_surname, life_status, interest_topics)