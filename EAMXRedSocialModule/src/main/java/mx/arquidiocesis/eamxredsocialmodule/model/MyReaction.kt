package mx.arquidiocesis.eamxredsocialmodule.model

data class MyReaction(
        val id: Int,
        val json: String,
        val active: Boolean,
        val img: String,
        val color: String,
        val type: String
)
