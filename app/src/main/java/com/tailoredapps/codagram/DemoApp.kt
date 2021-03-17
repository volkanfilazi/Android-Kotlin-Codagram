package com.tailoredapps.codagram

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin

class DemoApp: Application() {
    @KoinExperimentalAPI
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DemoApp)
            fragmentFactory()
            modules(appModules)
        }
    }

}