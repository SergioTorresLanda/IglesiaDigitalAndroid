package mx.arquidiocesis.eamxcommonutils.common

data class ModuleAdminEnabled(
    val id: Int,
    val name: String,
    val type: String,
    val imageUrl: String?=null,
    val modules: List<String>
)