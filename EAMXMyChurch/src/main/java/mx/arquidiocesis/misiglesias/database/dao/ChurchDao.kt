package mx.arquidiocesis.misiglesias.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.OnConflictStrategy.ROLLBACK
import androidx.room.Query
import mx.arquidiocesis.misiglesias.database.model.ChurchModelLocal

@Dao
interface ChurchDao {

    @Query("SELECT * FROM ChurchModelLocal WHERE userId= :userId")
    fun getAll(userId : Int) : List<ChurchModelLocal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChurchs(list : List<ChurchModelLocal>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChurch(item : ChurchModelLocal)

    @Query("DELETE FROM ChurchModelLocal WHERE userId= :userId AND isChurchMain = :isChurchMain")
    fun deleteChurchMain(userId: Int, isChurchMain : Boolean = true)

    @Query("DELETE FROM ChurchModelLocal WHERE userId= :userId AND idChurch = :idChurch")
    fun deleteChurchFavorite(userId: Int, idChurch: Int)

    @Query("DELETE FROM ChurchModelLocal WHERE userId= :userId")
    fun deleteAllChurch(userId : Int)
}