package com.ngmatt.weedmapsandroidcodechallenge.data.api

import com.ngmatt.weedmapsandroidcodechallenge.data.constants.API_KEY

private const val HEADER = "Bearer $API_KEY"

/**
 * YelpApiHelper is a class that houses all of the implemented Yelp APIs. All other repositories
 * will point to this helper class in order to interface with the service.
 * @param yelpApiService The Yelp API service to retrieve Yelp data.
 */
class YelpApiHelper(private val yelpApiService: YelpApiService) {
    suspend fun getBusinesses(term: String, latitude: Double, longitude: Double, offset: Int) =
        yelpApiService.getBusinesses(HEADER, term, latitude, longitude, offset = offset)
    suspend fun getReviews(id: String) = yelpApiService.getReviews(HEADER, id)
}