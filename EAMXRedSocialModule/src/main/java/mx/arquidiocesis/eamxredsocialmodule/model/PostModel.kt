package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    val id: Int,
    val statusId: Int,
    val reactionId:Int,
    var reaction: ReactionModel?,
    val content: String,
    val totalComments: Int,
    var totalReactions: Int?,
    val createdAt: String,
    var author: AuthorModel,
    val multimedia: List<MultimediaModel>,
    val scope: ScopeModel
) : Parcelable