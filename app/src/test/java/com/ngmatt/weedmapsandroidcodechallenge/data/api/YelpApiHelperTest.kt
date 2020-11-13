package com.ngmatt.weedmapsandroidcodechallenge.data.api

import com.ngmatt.weedmapsandroidcodechallenge.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class YelpApiHelperTest {

    private val yelpApiService: YelpApiService = mock()
    private val yelpApiHelper = YelpApiHelper(yelpApiService)

    @Test
    fun testBusinessSearch() {
        runBlockingTest {
            whenever(yelpApiService.getBusinesses(any(), any(), any(), any(), any(), any()))
                .thenReturn(defaultResponseBusinessResponse)
            val response = yelpApiHelper.getBusinesses(
                BUSINESS_SEARCH_TERM,
                BUSINESS_SEARCH_LATITUDE,
                BUSINESS_SEARCH_LONGITUDE,
                BUSINESS_SEARCH_OFFSET
            )
            assertEquals(defaultResponseBusinessResponse, response)
        }
    }

    @Test
    fun testReviewSearch() {
        runBlockingTest {
            whenever(yelpApiService.getReviews(any(), any())).thenReturn(
                defaultResponseReviewResponse
            )
            val response = yelpApiHelper.getReviews(REVIEW_SEARCH_ID)
            assertEquals(defaultResponseReviewResponse, response)
        }
    }

}