package com.akkayameva.soccerleague.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.data.db.Team
import com.akkayameva.soccerleague.data.repository.SoccerApiRepository
import com.akkayameva.soccerleague.data.repository.SoccerDBRepository
import com.akkayameva.soccerleague.ui.base.ApiResultUIModel
import com.akkayameva.soccerleague.ui.base.BaseViewModel
import kotlinx.coroutines.Job

import com.akkayameva.soccerleague.ui.base.Result

import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class SoccerViewModel(
    private val repository: SoccerApiRepository,
    private val dbRepository: SoccerDBRepository
) : BaseViewModel() {
    private var teamsJob: Job? = null
    private var fixtureJob: Job? = null

    private val _teamsState = MutableLiveData<ApiResultUIModel<List<Team>?>>()
    val teamsState: LiveData<ApiResultUIModel<List<Team>?>>
        get() = _teamsState

    private val _fixtureState = MutableLiveData<ApiResultUIModel<List<Fixture>?>>()
    val fixtureState: LiveData<ApiResultUIModel<List<Fixture>?>>
        get() = _fixtureState

    var listTeams: List<Team> = mutableListOf()
    var listTotalListTeams: MutableList<Team> = mutableListOf()
    var matchesMap: MutableMap<Int, FixtureData> = mutableMapOf()
    var matchesRound2Map: MutableMap<Int, FixtureData> = mutableMapOf()
    val listPassingTeams = mutableListOf<String>()
    var fixtureList = ArrayList<Fixture>()

    var showProgressLiveData = MutableLiveData<Boolean>()

    fun getTeams() {
        if (teamsJob?.isActive != true) {
            teamsJob = launchGetTeamsJob()
        }
    }

    fun getFixture(teamsCount: Int) {
        if (fixtureJob?.isActive != true) {
            fixtureJob = launchGetFixtureJob(teamsCount = teamsCount)
        }
    }

    fun listMatchesForTwoRounds() {
        val localMatchesMap = listMatchesRoundRobin()
        val swappedMatches = swapAwayAndHome(mapMatches = localMatchesMap)
        localMatchesMap.keys.forEach { key ->
            matchesMap[key] = localMatchesMap[key]!!
        }
        swappedMatches.keys.forEach { key ->
            matchesMap[key] = swappedMatches[key]!!
        }
    }

    fun swapAwayAndHome(mapMatches: Map<Int, FixtureData>): Map<Int, FixtureData> {
        val matches = mutableMapOf<Int, FixtureData>()
        var day = mapMatches.keys.size
        mapMatches.keys.forEach { key ->
            val data = mapMatches[key]
            val listMatches = mutableListOf<MatchData>()
            data?.listMatches?.forEach {
                listMatches.add(
                    MatchData(
                        teamHome = it.teamAway,
                        teamAway = it.teamHome
                    )
                )
            }
            matches[day] = FixtureData(
                listMatches = listMatches, passingTeam = data?.passingTeam,
                round = day
            )
            day++
        }
        return matches
    }

    fun listMatchesRoundRobin(): Map<Int, FixtureData> {
        val matchesMap = mutableMapOf<Int, FixtureData>()
        val count = listTeams.size
        val listFixture = if (count % 2 == 0) {
            repository.generateFixtureForDual(teamCount = count)
        } else {
            repository.generateFixtureForSingle(count)
        }

        val totalDaysCount = if (count % 2 == 0) {
            count - 1
        } else {
            count
        }

        for (dayCount in 0 until totalDaysCount) {

            val roundFixtures = listFixture.filter {
                it.roundCount == dayCount
            }

            val fixtureData = FixtureData()
            val listMatches = mutableListOf<MatchData>()

            roundFixtures.forEach {
                Timber.tag(TAG).v("pass team : %s", it.passTeam)
                listMatches.add(
                    MatchData(
                        teamHome = listTeams[it.homeTeam!!].team_name!!,
                        teamAway = listTeams[it.awayTeam!!].team_name!!,
                    )
                )
                fixtureData.round = it.roundCount
                if (it.passTeam != null) {
                    fixtureData.passingTeam = listTeams[it.passTeam!!].team_name!!
                }
            }

            fixtureData.listMatches = listMatches
            matchesMap[dayCount] = fixtureData
        }
        return matchesMap
    }

//    fun calculateFixForSingle(listTeams: List<Team>): List<MatchData> {
//        val n = listTeams.size
//        val rounds_count = n - 1
//        val teams_count = listTeams.size
//        val listMatches = mutableListOf<MatchData>()
//        for (round in 0 until rounds_count) {
//            for (match in 0 until 2) {
//                val home = (round + match) % (teams_count)
//                var away = (teams_count - 1 - match + round) % (teams_count - 1);
//
//                if (match == 0)
//                    away = teams_count - 1
//
//                listMatches.add(
//                    MatchData(
//                        teamHome = listTeams[home].team_name ?: "",
//                        teamAway = listTeams[away].team_name ?: ""
//                    )
//                )
//            }
//        }
//        return listMatches
//    }


//    fun listMatches(listTeamsParam: List<String>) {
//        val listPlayingTeams = mutableListOf<String>()
//
//        val listTeams = listTeamsParam.toMutableList()
//
//        listTeams.shuffle()
//
//        val numTeams = listTeams.size
//
//        if (listTeams.size % 2 != 0) {
//            listTeams.add("Bye"); // If odd number of teams add a dummy
//        }
//
//        val numDays = (numTeams - 1) // Days needed to complete tournament
//        val halfSize = numTeams / 2;
//
//        val teams = mutableListOf<String>();
//
//        teams.addAll(listTeams); // Add teams to List and remove the first team
//        teams.removeAt(0)
//
//        val teamsSize = teams.size
//
//        for (day in 0 until numDays) {
//            Timber.tag(TAG).v("Day {%s}", (day + 1))
//
//            val teamIdx = day % teamsSize
//
//            Timber.tag(TAG).v("{%s} vs {%s}", teams[teamIdx], listTeams[0])
//
//            val listMatches = mutableListOf<MatchData>()
//            for (idx in 1 until halfSize) {
//                val firstTeam = (day + idx) % teamsSize;
//                val secondTeam = (day + teamsSize - idx) % teamsSize
//                listMatches.add(
//                    MatchData(
//                        teamHome = listTeams[firstTeam],
//                        teamAway = listTeams[secondTeam]
//                    )
//                )
//                listPlayingTeams.add(teams[firstTeam])
//                listPlayingTeams.add(teams[secondTeam])
//
//                Timber.tag(TAG).v("{%s} vs {%s}", teams[firstTeam], teams[secondTeam]);
//            }
//
//            listPassingTeams.add(teams[teamIdx])
//
//            matchesMap[day] = listMatches
//        }
//    }


    private fun launchGetTeamsJob(): Job {
        return runAsync2(result = _teamsState) {
            when (val result = repository.getTeams()) {
                is Result.Success -> {
                    listTeams = result.data
                    listTotalListTeams = result.data.toMutableList()
                    dbRepository.saveTeams(result.data)
                    return@runAsync2 result.data
                }
                is Result.Error -> {
                    throw result.throwable
                }
            }
        }
    }

    private fun launchGetFixtureJob(teamsCount: Int): Job {
        return runAsync2(result = _fixtureState) {
            val listFixture = repository.createFixture(teamsCount)
            dbRepository.saveFixture(listFixture)
            listFixture
        }
    }

    suspend fun createFixture(count: Int) {
        fixtureList = if (count % 2 == 0) {
            repository.generateFixtureForDual(count)

        } else {
            repository.generateFixtureForSingle(count)
        }

        dbRepository.saveFixture(fixtureList)
    }
}

data class MatchData(
    var teamHome: String,
    var teamAway: String
)

data class FixtureData(
    var listMatches: List<MatchData>? = mutableListOf(),
    var passingTeam: String? = "",
    var round: Int? = 0
)
