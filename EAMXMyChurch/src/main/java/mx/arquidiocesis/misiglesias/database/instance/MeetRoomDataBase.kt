package mx.arquidiocesis.misiglesias.database.instance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.arquidiocesis.misiglesias.database.dao.ChurchDao
import mx.arquidiocesis.misiglesias.database.model.ChurchModelLocal

@Database(entities = [ChurchModelLocal::class], version = 4, exportSchema = false)
abstract class MeetRoomDataBase : RoomDatabase() {

    abstract fun churchDao() : ChurchDao

    companion object {

        @Synchronized
        fun getRoomInstance(context: Context): MeetRoomDataBase =
             Room.databaseBuilder(
                    context.applicationContext,
                    MeetRoomDataBase::class.java,
                    "EncuentroDataBase"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}