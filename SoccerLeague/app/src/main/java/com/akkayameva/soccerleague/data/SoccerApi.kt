package com.akkayameva.soccerleague.data

import com.akkayameva.soccerleague.data.db.Team
import retrofit2.Response
import retrofit2.http.GET


interface SoccerApi {

    //for test team different size, 5 team
//    @GET("/teamTest")
//    suspend fun getTeams(): Response<List<Team>>

    //all teams, 21 team
    @GET("/teams")
    suspend fun getTeams(): Response<List<Team>>
}

