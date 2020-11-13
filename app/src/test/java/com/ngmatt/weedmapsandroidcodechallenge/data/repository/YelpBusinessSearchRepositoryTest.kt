package com.ngmatt.weedmapsandroidcodechallenge.data.repository

import com.ngmatt.weedmapsandroidcodechallenge.*
import com.ngmatt.weedmapsandroidcodechallenge.data.api.YelpApiHelper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class YelpBusinessSearchRepositoryTest {

    private val yelpApiHelper: YelpApiHelper = mock()
    private val yelpBusinessSearchRepository = YelpBusinessSearchRepository(yelpApiHelper)

    @Test
    fun testBusinessSearch() {
        runBlockingTest {
            whenever(yelpApiHelper.getBusinesses(any(), any(), any(), any())).thenReturn(
                defaultResponseBusinessResponse
            )
            val response = yelpBusinessSearchRepository.getBusinesses(BUSINESS_SEARCH_TERM, BUSINESS_SEARCH_LATITUDE, BUSINESS_SEARCH_LONGITUDE, BUSINESS_SEARCH_OFFSET)
            Assert.assertEquals(defaultResponseBusinessResponse, response)
        }
    }

    @Test
    fun testReviewSearch() {
        runBlockingTest {
            whenever(yelpApiHelper.getReviews(any())).thenReturn(defaultResponseReviewResponse)
            val response = yelpBusinessSearchRepository.getReviews(REVIEW_SEARCH_ID)
            Assert.assertEquals(defaultResponseReviewResponse, response)
        }
    }
}