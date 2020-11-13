package com.ngmatt.weedmapsandroidcodechallenge.data.api

import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessResponse
import com.ngmatt.weedmapsandroidcodechallenge.data.model.ReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val ENDPOINT_BUSINESS_SEARCH = "businesses/search"
private const val ENDPOINT_BUSINESS_REVIEW = "businesses/{id}/reviews"
private const val HEADER_AUTHORIZATION = "Authorization"
private const val PATH_ID = "id"
private const val QUERY_TERM = "term"
private const val QUERY_LATITUDE = "latitude"
private const val QUERY_LONGITUDE = "longitude"
private const val QUERY_LIMIT = "limit"
private const val QUERY_LIMIT_DEFAULT = 4
private const val QUERY_OFFSET = "offset"

/**
 * YelpApiService is the interface that defines interaction with the entirety of the Yelp APIs.
 */
interface YelpApiService {

    @GET(ENDPOINT_BUSINESS_SEARCH)
    suspend fun getBusinesses(
        @Header(HEADER_AUTHORIZATION) authHeader: String,
        @Query(QUERY_TERM) searchTerm: String,
        @Query(QUERY_LATITUDE) latitude: Double,
        @Query(QUERY_LONGITUDE) longitude: Double,
        @Query(QUERY_LIMIT) limit: Int = QUERY_LIMIT_DEFAULT,
        @Query(QUERY_OFFSET) offset: Int
    ) : Response<BusinessResponse>

    @GET(ENDPOINT_BUSINESS_REVIEW)
    suspend fun getReviews(
        @Header(HEADER_AUTHORIZATION) authHeader: String,
        @Path(PATH_ID) id: String
    ) : Response<ReviewResponse>

}