package com.akkayameva.soccerleague.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "table_fixture")
@Parcelize
data class Fixture(
    @PrimaryKey(autoGenerate = false) var uid: Int? = null,
    @ColumnInfo(name = "round_count")
    var roundCount: Int? = null,
    @ColumnInfo(name = "home_team")
    var homeTeam: Int? = null,
    @ColumnInfo(name = "away_team")
    var awayTeam: Int? = null,
    @ColumnInfo(name = "pass_team")
    var passTeam: Int? = null
) : Parcelable
