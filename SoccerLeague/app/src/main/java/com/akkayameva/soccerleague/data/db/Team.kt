package com.akkayameva.soccerleague.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(tableName = "table_teams")
@Parcelize
@JsonClass(generateAdapter = true)
data class Team(
    @PrimaryKey(autoGenerate = false)
    val team_id: Int? = null,
    val team_name: String? = null
) : Parcelable
