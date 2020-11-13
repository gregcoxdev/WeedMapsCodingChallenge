package com.ngmatt.weedmapsandroidcodechallenge.di.modules

import com.ngmatt.weedmapsandroidcodechallenge.data.api.YelpApiHelper
import org.koin.dsl.module

val helperModule = module {
    factory { YelpApiHelper(get()) }
}
