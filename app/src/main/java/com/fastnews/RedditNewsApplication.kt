package com.fastnews

import android.app.Application
import com.fastnews.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RedditNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupDi()
    }

    private fun setupDi(){
        startKoin {
            androidContext(this@RedditNewsApplication)
                .modules(listOf(viewModelModule))
        }
    }
}