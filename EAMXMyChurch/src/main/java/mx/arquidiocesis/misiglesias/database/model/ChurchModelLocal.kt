package mx.arquidiocesis.misiglesias.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChurchModelLocal(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val idChurch : Int,
    var userId : Int,
    val isChurchMain: Boolean,
    val name: String,
    val image_url: String,
    val arrayImage: String? = null
)