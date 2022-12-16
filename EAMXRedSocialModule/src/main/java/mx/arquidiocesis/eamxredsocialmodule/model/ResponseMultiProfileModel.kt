package mx.arquidiocesis.eamxredsocialmodule.model

data class ResponseMultiProfileModel(
    val message: String,
    val requestId: String,
    val result: List<ResultMultiProfileModel>?
)