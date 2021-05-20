package com.akkayameva.soccerleague.data.repository

import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.data.db.Team
import com.akkayameva.soccerleague.data.db.TeamDatabase

class SoccerDBRepository(private val database: TeamDatabase) {
    suspend fun saveTeams(listTeams: List<Team>) {
        listTeams.forEach {
            database.getTeamDao().saveTeams(it)
        }
    }

    fun getSavedTeams() = database.getTeamDao().getAllTeams()
    suspend fun saveFixture(listFixture: List<Fixture>) {
        listFixture.forEach {
            database.getTeamDao().saveFixture(it)
        }
    }

    fun getSavedFixture() = database.getTeamDao().getAllFixture()
    suspend fun deleteAllFixture() = database.getTeamDao().deleteAllFixture()
    fun getRoundList(roundCount: Int) = database.getTeamDao().getRoundList(roundCount)

    suspend fun getRoundListSync(roundCount: Int) = database.getTeamDao().getRoundListSync(roundCount)
}