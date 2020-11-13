package com.ngmatt.weedmapsandroidcodechallenge.ui.main.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ngmatt.weedmapsandroidcodechallenge.*
import com.ngmatt.weedmapsandroidcodechallenge.data.model.Resource
import com.ngmatt.weedmapsandroidcodechallenge.data.repository.YelpBusinessSearchRepository
import com.ngmatt.weedmapsandroidcodechallenge.ui.viewmodel.YelpSearchViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class YelpSearchViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val yelpBusinessSearchRepository: YelpBusinessSearchRepository = mock()
    private val yelpSearchViewModel =
        YelpSearchViewModel(yelpBusinessSearchRepository, testCoroutineDispatcher)

    @Before
    fun setUp() {
        mockkStatic(Log::class).apply {
            every { Log.d(any(), any()) } returns 0
            every { Log.e(any(), any()) } returns 0
        }
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun testGetBusinessBatch() {
        runBlockingTest {
            whenever(
                yelpBusinessSearchRepository.getBusinesses(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).thenReturn(
                defaultResponseBusinessResponse
            )
            whenever(yelpBusinessSearchRepository.getReviews(any())).thenReturn(
                defaultResponseReviewResponse
            )
            val businessBatch = yelpSearchViewModel.getBusinessBatch(
                DEFAULT_BUSINESS_NAME,
                BUSINESS_SEARCH_LATITUDE,
                BUSINESS_SEARCH_LONGITUDE,
                BUSINESS_SEARCH_OFFSET
            )
            assertEquals(defaultBusiness, businessBatch.businessData[0].business)
            assertEquals(defaultReview, businessBatch.businessData[0].reviews[0])
        }
    }

    @Test
    fun testSearchForBusinessesLoading() {
        runBlockingTest {
            val businessSearch = yelpSearchViewModel.searchForBusinesses(
                DEFAULT_BUSINESS_NAME,
                BUSINESS_SEARCH_LATITUDE,
                BUSINESS_SEARCH_LONGITUDE,
                BUSINESS_SEARCH_OFFSET
            )
            val resourceBusinessBatch = businessSearch.blockingObserve()
            assertEquals(Resource.loading(null), resourceBusinessBatch)
        }
    }
}
