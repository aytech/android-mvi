package com.oleg.androidmvi

import android.app.Application
import com.oleg.androidmvi.data.db.MovieDatabase
import timber.log.Timber

class App : Application() {

    lateinit var db: MovieDatabase

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        db = MovieDatabase.getInstance(application = this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        INSTANCE = this
    }

}