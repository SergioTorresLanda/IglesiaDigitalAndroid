package mx.arquidiocesis.eamxprofilemodule.model.update.userupdatediacono

import com.google.gson.annotations.Expose
import mx.arquidiocesis.eamxprofilemodule.model.update.base.*

class UserDiacono (
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
        val locations: List<location>,
        @Expose
        val prefix: Prefix,
): BaseUpdate(id, username, email, first_surname, name, phone_number, second_surname, life_status, interest_topics)