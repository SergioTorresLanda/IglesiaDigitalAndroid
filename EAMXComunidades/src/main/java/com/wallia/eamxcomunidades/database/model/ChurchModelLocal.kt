package com.wallia.eamxcomunidades.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CommunityLocal(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    val idCommunity : Int,
    val userId : Int,
    val isCommunityMain: Boolean,
    val name: String,
    val image_url: String,
    val arrayImage: String? = null
)
