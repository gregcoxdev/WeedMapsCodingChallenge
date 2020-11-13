package com.ngmatt.weedmapsandroidcodechallenge.di.modules

import com.ngmatt.weedmapsandroidcodechallenge.data.api.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val INTERCEPTOR_OFFLINE = "OFFLINE"
private const val INTERCEPTOR_NETWORK = "NETWORK"

val networkModule = module {
    factory(named(INTERCEPTOR_OFFLINE)) { provideOfflineInterceptor(get()) }
    factory(named(INTERCEPTOR_NETWORK)) { provideNetworkInterceptor() }
    factory { provideOkHttpClient(get(), get(qualifier = named(INTERCEPTOR_OFFLINE)), get(qualifier = named(
        INTERCEPTOR_NETWORK
    )
    )) }
    factory { provideMoshi() }
    single { provideRetrofit(get(), get()) }
    factory { provideYelpApiService(get()) }
}