package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion

data class Data(
    val header: List<Header>,
    val pages: Int,
    val resp: List<Resp>,
    val total: Int
)