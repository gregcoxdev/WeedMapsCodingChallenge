package com.ngmatt.weedmapsandroidcodechallenge.data.api

import android.content.Context
import com.ngmatt.weedmapsandroidcodechallenge.data.api.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import retrofit2.Response

@ExperimentalCoroutinesApi
class RetrofitBuilderTest {

    @Test
    fun testBusinessSearch() {
        val context: Context = mock()
        val offlineInterceptor = provideOfflineInterceptor(context)
        assertNotNull(offlineInterceptor)
        val networkInterceptor = provideNetworkInterceptor()
        assertNotNull(networkInterceptor)
        val okHttpClient = provideOkHttpClient(context, offlineInterceptor, networkInterceptor)
        assertNotNull(okHttpClient)
        val moshi = provideMoshi()
        assertNotNull(moshi)
        val retrofit = provideRetrofit(okHttpClient, moshi)
        assertNotNull(retrofit)
        val yelpApiService = provideYelpApiService(retrofit)
        assertNotNull(yelpApiService)

    }

}