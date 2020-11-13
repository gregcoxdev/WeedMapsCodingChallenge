package com.ngmatt.weedmapsandroidcodechallenge.data.api

import android.content.Context
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.*
import com.ngmatt.weedmapsandroidcodechallenge.utils.Utils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val HEADER_CACHE_CONTROL = "Cache-Control"
private const val HEADER_PRAGMA = "Pragma"

/**
 * Provide the OkHttpClient to the network module. The timeouts are defaulted to
 * [DEFAULT_NETWORK_TIMEOUT] so that we have the ability to configure it in the code. There is also
 * a network interceptor and an offline interceptor used to cache the data.
 * @param context The application context.
 * @return The OkHttpClient to inject into the Retrofit service.
 */
fun provideOkHttpClient(context: Context,
                        offlineInterceptor: Interceptor,
                        networkInterceptor: Interceptor): OkHttpClient =
    OkHttpClient.Builder()
        .cache(Cache(context.cacheDir, DEFAULT_CACHE_SIZE))
        .connectTimeout(DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .callTimeout(DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .addNetworkInterceptor(networkInterceptor)
        .addInterceptor(offlineInterceptor)
        .build()

/**
 * Provide the offline interceptor used for caching when there is no internet connection available.
 * At this point, it will cache for [DEFAULT_CACHE_STALE_TIME] when there is no internet available.
 * @param context The application context.
 * @return An interceptor to be used with the OkHttpClient.
 */
fun provideOfflineInterceptor(context: Context): Interceptor =
    Interceptor { chain ->
        var request: Request = chain.request()
        if (!Utils.isInternetAvailable(context)) {
            val cacheControl = CacheControl.Builder()
                .maxStale(DEFAULT_CACHE_STALE_TIME, TimeUnit.DAYS)
                .build()
            request = request.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .cacheControl(cacheControl)
                .build()
        }
        chain.proceed(request)
}

/**
 * Provide the network interceptor used for caching when there is a valid connection available. At
 * this point it only caches [DEFAULT_AGE_TIME] hours before invalidation. This could be set to 15
 * days per the requirement, but I believe the stale time should be used for the offline cache
 * interceptor.
 * @return A network interceptor to be used with the OkHttpClient.
 */
fun provideNetworkInterceptor(): Interceptor = Interceptor { chain ->
    val response: Response = chain.proceed(chain.request())
    val cacheControl = CacheControl.Builder()
        .maxAge(DEFAULT_AGE_TIME, TimeUnit.MINUTES)
        .build()
    response.newBuilder()
        .removeHeader(HEADER_PRAGMA)
        .removeHeader(HEADER_CACHE_CONTROL)
        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
        .build()
}

/**
 * Provide the Moshi instance used to parse response objects into their respective data class.
 * @return The Moshi instance to be injected into the [MoshiConverterFactory].
 */
fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * Provide the Retrofit instance used to interact with the Yelp endpoints.
 * @param okHttpClient The OkHttpClient used to interact with the network aspect.
 * @param moshi The Moshi instance used to parse responses.
 * @return The Retrofit instance to be used with the [YelpApiService].
 */
fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

/**
 * Provide the Yelp API service to be used by repositories to interact with the Yelp endpoints.
 * @param retrofit The Retrofit instance tied to the Yelp APIs.
 * @return The Yelp API service to be injected into their respective repositories.
 */
fun provideYelpApiService(retrofit: Retrofit): YelpApiService =
    retrofit.create(YelpApiService::class.java)
