package com.ngmatt.weedmapsandroidcodechallenge.di.modules

import com.ngmatt.weedmapsandroidcodechallenge.data.repository.YelpBusinessSearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { YelpBusinessSearchRepository(get()) }
}