package mx.arquidiocesis.eamxredsocialmodule.model.like

data class EAMXLikeRequest(
    var post_id: Int,
    var reaction_id: Int = 23,
    var FIIDEMPLEADO: Int = 100,
    var `as`: String = "Person"
)
