package com.akkayameva.soccerleague.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TeamDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTeams(vararg teams: Team)

    @Query("Select *FROM table_teams")
    fun getAllTeams(): LiveData<List<Team>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveFixture(vararg fixture: Fixture)

    @Query("Select *FROM table_fixture")
    fun getAllFixture(): LiveData<List<Fixture>>

    @Query("DELETE FROM  table_fixture")
    suspend fun deleteAllFixture()

    @Query("SELECT *  FROM table_fixture WHERE round_count LIKE:roundCount")
    fun getRoundList(roundCount: Int): LiveData<List<Fixture>>

    @Query("SELECT *  FROM table_fixture WHERE round_count LIKE:roundCount")
    suspend fun getRoundListSync(roundCount: Int): List<Fixture>

}