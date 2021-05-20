package com.akkayameva.soccerleague.data.repository

import com.akkayameva.soccerleague.data.Apifactory
import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.data.db.Team

import com.akkayameva.soccerleague.ui.base.Result
import com.akkayameva.soccerleague.ui.base.SoccerThrowable
import kotlin.collections.ArrayList


class SoccerApiRepository(private val apifactory: Apifactory) {
    suspend fun getTeams(): Result<List<Team>> {
        val response = apifactory.api.getTeams()
        return if (response.isSuccessful) {
            Result.Success(data = response.body()!!)
        } else {
            Result.Error(SoccerThrowable(errorCode = 0, "Some thing wrong happened"))
        }
    }

    fun createFixture(count: Int): List<Fixture> {
        return if (count % 2 == 0) {
            generateFixtureForDual(count)

        } else {
            generateFixtureForSingle(count)
        }
    }

    fun generateFixtureForDual(teamCount: Int): ArrayList<Fixture> {
        val fixtureList = ArrayList<Fixture>()
        val roundCount = teamCount - 1
        val matchCountPerRound = teamCount / 2
        var list = ArrayList<Int>()
        for (i in 0 until teamCount) {
            list.add(i)
        }
        list.shuffle()

        for (i in 0 until roundCount) {
            for (j in 0 until matchCountPerRound) {
                val secondIndex = (teamCount - 1) - j
                val fixtureInstance = Fixture()
                fixtureInstance.apply {
                    homeTeam = list[j]
                    awayTeam = list[secondIndex]
                    this.roundCount = i
                }
                fixtureList.add(fixtureInstance)
            }
            val newList = ArrayList<Int>()
            newList.add(list[0])
            newList.add(list[list.size - 1])
            for (k in 1 until list.size - 1) {
                newList.add(list[k])
            }
            list = newList
        }

        return fixtureList
    }

    fun generateFixtureForSingle(teamCountParam: Int): ArrayList<Fixture> {
        val fixtureList = ArrayList<Fixture>()
        val bayList = ArrayList<Int>()
        val teamCount = teamCountParam + 1
        val roundCount = teamCount - 1
        val matchCountPerRound = teamCount / 2

        var list = ArrayList<Int>()

        for (i in 0 until teamCount) {
            list.add(i)
        }
        list.shuffle()

        val temp = teamCount - 1

        for (i in 0 until roundCount) {
            for (j in 0 until matchCountPerRound) {
                val secondIndex = (teamCount - 1) - j
                if (temp == list[j]) {
                    bayList.add(list[secondIndex])
                    continue
                }

                if (temp == list[secondIndex]) {
                    bayList.add(list[j])
                    continue
                }

                val fixtureInstance = Fixture()
                fixtureInstance.apply {
                    homeTeam = list[j]
                    awayTeam = list[secondIndex]
                    this.roundCount = i
                }
                fixtureList.add(fixtureInstance)
            }

            val newList = ArrayList<Int>()
            newList.add(list[0])
            newList.add(list[list.size - 1])
            for (k in 1 until list.size - 1) {
                newList.add(list[k])
            }
            list = newList
        }
        fixtureList.forEach {
            it.passTeam = bayList[it.roundCount!!]
        }
        return fixtureList
    }
}