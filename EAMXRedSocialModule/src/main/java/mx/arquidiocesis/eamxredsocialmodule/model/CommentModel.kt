package mx.arquidiocesis.eamxredsocialmodule.model

data class CommentModel(
    val id: Int,
    val totalComments: Int,
    val totalReactions:Int,
    val content: String,
    val status: Int,
    val createdAt: String,
    var author: AuthorModel,
    val scope: ScopeModel
)