package com.ngmatt.weedmapsandroidcodechallenge.data.repository

import com.ngmatt.weedmapsandroidcodechallenge.data.api.YelpApiHelper

/**
 * YelpBusinessSearchRepository houses all the particular APIs that are needed to perform a business
 * search.
 * @param yelpApiHelper The helper class used to interface with Yelp APIs.
 */
class YelpBusinessSearchRepository(private val yelpApiHelper: YelpApiHelper) {
    suspend fun getBusinesses(term: String, latitude: Double, longitude: Double, offset: Int) =
        yelpApiHelper.getBusinesses(term, latitude, longitude, offset)
    suspend fun getReviews(id: String) = yelpApiHelper.getReviews(id)
}