package mx.arquidiocesis.eamxredsocialmodule.news.create.model

data class EAMXMultimedia(
    val id: Long? = null,

    var mimeType: String? = null,

    //FireStoreUrl
    var url: String? = null,
    //FireStoreUrl
    var preSigned: String? = null,
    // For local use
    var displayName: String? = null,

    var size: Long = 0,

    var path: String? = null,
) {
    fun isImage(): Boolean = mimeType?.contains("image") ?: false
}
