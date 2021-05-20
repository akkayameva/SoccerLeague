package com.akkayameva.soccerleague.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [Team::class, Fixture::class],
    version = 1,
    exportSchema = false
)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun getTeamDao(): TeamDAO

    companion object {
        @Volatile
        private var instance: TeamDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context) = instance ?: synchronized(LOCK) {

            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context,
            TeamDatabase::class.java,
            "team_db.db"
        ).build()

    }
}