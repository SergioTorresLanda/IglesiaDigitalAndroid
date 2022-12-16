package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion

data class Reaction(
    val active: Boolean,
    val color: String,
    val id: Int,
    val img: String,
    val json: String,
    val total: Int,
    val type: String
)