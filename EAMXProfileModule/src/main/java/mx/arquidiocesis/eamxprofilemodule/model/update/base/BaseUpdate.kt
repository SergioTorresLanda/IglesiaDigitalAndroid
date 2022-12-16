package mx.arquidiocesis.eamxprofilemodule.model.update.base

import com.google.gson.annotations.Expose

open class BaseUpdate  constructor(
    @Expose
    val id: Int,
    @Expose
    val username: String,
    @Expose
    val email: String,
    @Expose
    val first_surname: String,
    @Expose
    val name: String,
    @Expose
    val phone_number: String,
    @Expose
    val second_surname: String,
    @Expose
    val life_status: LifeStatus,
    @Expose
    val interest_topics: List<InterestTopic>
)
