package com.wallia.eamxcomunidades.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wallia.eamxcomunidades.database.model.CommunityLocal

@Dao
interface CommunityDao {

    @Query("SELECT * FROM CommunityLocal WHERE userId=:userId")
    fun getAll(userId: Int) : List<CommunityLocal>

    @Insert
    fun addCommunities(list : List<CommunityLocal>)

    @Insert
    fun addCommunity(item : CommunityLocal)

    @Query("DELETE FROM CommunityLocal WHERE userId= :userId AND isCommunityMain = :isCommunityMain")
    fun deleteCommunityMain(userId: Int, isCommunityMain : Boolean = true)

    @Query("DELETE FROM CommunityLocal WHERE userId= :userId AND idCommunity = :idCommunity")
    fun deleteCommunity(userId: Int, idCommunity: Int)
}