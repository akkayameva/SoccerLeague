package com.akkayameva.soccerleague.di

import com.akkayameva.soccerleague.data.Apifactory
import com.akkayameva.soccerleague.data.db.TeamDatabase
import com.akkayameva.soccerleague.data.repository.SoccerApiRepository
import com.akkayameva.soccerleague.data.repository.SoccerDBRepository
import com.akkayameva.soccerleague.ui.viewmodel.SoccerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val soccerModule: Module = module {
    viewModel { SoccerViewModel(get(), get()) }
    single { SoccerApiRepository(get()) }
    single { Apifactory() }
    single { SoccerDBRepository(get()) }
    single { TeamDatabase.getInstance(androidContext()) }
}