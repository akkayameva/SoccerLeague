package com.akkayameva.soccerleague.ui.base

import android.app.Application
import android.content.Context
import com.akkayameva.soccerleague.di.soccerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class SoccerApp : Application() {

    companion object {
        fun startKoin(applicationContext: Context) {
            startKoin {
                // Android context
                androidContext(applicationContext)
                modules(listOf(soccerModule))
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin(this)
    }
}