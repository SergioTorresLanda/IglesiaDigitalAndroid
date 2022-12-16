package com.wallia.eamxcomunidades.database.instance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wallia.eamxcomunidades.database.dao.CommunityDao
import com.wallia.eamxcomunidades.database.model.CommunityLocal

@Database(entities = [CommunityLocal::class], version = 3, exportSchema = false)
abstract class MeetRoomDataBaseCommunity : RoomDatabase() {

    abstract fun communityDao() : CommunityDao

    companion object {

        @Synchronized
        fun getRoomInstance(context: Context): MeetRoomDataBaseCommunity =
             Room.databaseBuilder(
                    context.applicationContext,
                    MeetRoomDataBaseCommunity::class.java,
                    "EncuentroDataBase_Community"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}