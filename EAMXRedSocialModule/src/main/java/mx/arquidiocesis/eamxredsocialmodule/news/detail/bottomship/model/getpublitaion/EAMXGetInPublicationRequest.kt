package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion

data class EAMXGetInPublicationRequest(
    val FIIDEMPLEADO: String = "100",
    val post_id: Int,
    val skip: Int = 0,
    val limit: Int = 20
)
