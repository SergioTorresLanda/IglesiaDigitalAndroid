package mx.arquidiocesis.eamxprofilemodule.model.update.userupdatecaselaicconsecratedreligious

import com.google.gson.annotations.Expose
import mx.arquidiocesis.eamxprofilemodule.model.update.base.*

class UserLaicConsecratedReligious(
    id: Int,
    username: String,
    email: String,
    first_surname: String,
    name: String,
    phone_number: String,
    @Expose
    val location_id: Int?,
    second_surname: String,
    life_status: LifeStatus,
    interest_topics: List<InterestTopic>,
    @Expose
    val congregation: Congregation?,
    @Expose
    val pastoral_work: String,
    @Expose
    val profile: String,
    @Expose
    val prefix: Prefix,
) : BaseUpdate(
    id,
    username,
    email,
    first_surname,
    name,
    phone_number,
    second_surname,
    life_status,
    interest_topics
)