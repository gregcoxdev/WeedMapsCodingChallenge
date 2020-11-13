package com.ngmatt.weedmapsandroidcodechallenge.di.modules

import com.ngmatt.weedmapsandroidcodechallenge.ui.viewmodel.YelpSearchViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val viewModelModule = module {
    factory { YelpSearchViewModel(get(), Dispatchers.IO) }
}
