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
    var matchesMap: MutableMap<Int, List<MatchData>> = mutableMapOf()
    val listPassingTeams = mutableListOf<String>()
    var fixtureList = ArrayList<Fixture>()

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


    fun listMatches(listTeamsParam: List<String>) {

        Collections.shuffle(listTeamsParam)

        val listTeams = listTeamsParam.toMutableList()
        val numTeams = listTeams.size

        if (listTeams.size % 2 != 0) {
            listTeams.add("Bye"); // If odd number of teams add a dummy
        }

        val numDays = (numTeams - 1); // Days needed to complete tournament
        val halfSize = numTeams / 2;

        val teams = mutableListOf<String>();

        teams.addAll(listTeams); // Add teams to List and remove the first team
        teams.removeAt(0)

        val teamsSize = teams.size

        for (day in 0 until numDays) {
            Timber.tag(TAG).v("Day {%s}", (day + 1))

            val teamIdx = day % teamsSize

            Timber.tag(TAG).v("{%s} vs {%s}", teams[teamIdx], listTeams[0]);
            if (teams[0] == "Bye") {
                listPassingTeams.add(teams[teamIdx])
            }

            val listMatches = mutableListOf<MatchData>()
            for (idx in 1 until halfSize) {
                val firstTeam = (day + idx) % teamsSize;
                val secondTeam = (day + teamsSize - idx) % teamsSize
                listMatches.add(
                    MatchData(
                        teamHome = listTeams[firstTeam],
                        teamAway = listTeams[secondTeam]
                    )
                )
                if (teams[secondTeam] == "Bye") {
                    listPassingTeams.add(teams[firstTeam])
                }
                Timber.tag(TAG).v("{%s} vs {%s}", teams[firstTeam], teams[secondTeam]);
            }

            matchesMap[day] = listMatches

        }
    }


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
    var listMatches: List<MatchData>,
    var passingTeam: Team
)