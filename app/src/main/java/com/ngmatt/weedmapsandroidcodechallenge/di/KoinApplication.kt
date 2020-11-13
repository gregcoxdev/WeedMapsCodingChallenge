package com.ngmatt.weedmapsandroidcodechallenge.di

import android.app.Application
import com.ngmatt.weedmapsandroidcodechallenge.di.modules.*
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.adapterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * KoinApplication is used to start up the dependency injection capability within the application
 * by invoking [startKoin].
 */
class KoinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(listOf(fragmentModule,
                viewModelModule,
                repositoryModule,
                helperModule,
                networkModule,
                adapterModule))
        }
    }
}