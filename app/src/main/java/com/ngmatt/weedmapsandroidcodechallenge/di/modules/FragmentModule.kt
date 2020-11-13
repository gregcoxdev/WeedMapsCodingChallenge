package com.ngmatt.weedmapsandroidcodechallenge.di.modules

import com.ngmatt.weedmapsandroidcodechallenge.ui.view.YelpSearchFragment
import org.koin.dsl.module

val fragmentModule = module {
    factory { YelpSearchFragment() }
}