package mx.arquidiocesis.registrosacerdote.model.update.base

import com.google.gson.annotations.Expose
import mx.arquidiocesis.registrosacerdote.model.catalog.InterestTopic

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
    val life_status: BaseOnlyId,
    @Expose
    val interest_topics: List<InterestTopic>
)
