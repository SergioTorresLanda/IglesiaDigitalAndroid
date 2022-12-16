package mx.arquidiocesis.eamxredsocialmodule.model

data class ResponseMultimediaModel(
    val filename: String,
    val pre_signed: String,
    val filekey: String)