package com.ngmatt.weedmapsandroidcodechallenge.ui.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.API_TIME_LIMIT_MILLIS
import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessBatch
import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessData
import com.ngmatt.weedmapsandroidcodechallenge.data.model.Resource
import com.ngmatt.weedmapsandroidcodechallenge.data.repository.YelpBusinessSearchRepository
import kotlinx.coroutines.*

private const val TAG = "YelpSearchViewModel"

class YelpSearchViewModel(private val repository: YelpBusinessSearchRepository, private val dispatcher: CoroutineDispatcher) : ViewModel() {

    /**
     * Perform a business search, given a term, location and offset for pagination. This will be
     * reported back to the view via LiveData. The requests are delayed in order to ensure the API
     * limit is not exceeded. There are other ways to avoid the request limitation but this was
     * chosen for simplicity.
     * @param term The query term to search.
     * @param latitude The latitude of the location to search.
     * @param longitude The longitude of the location to search.
     * @param offset The offset of the request for pagination.
     * @return LiveDataScope to observe from the view.
     */
    fun searchForBusinesses(term: String, latitude: Double, longitude: Double, offset: Int) = liveData(dispatcher) {
        emit(Resource.loading(null))
        delay(API_TIME_LIMIT_MILLIS)
        try {
            val businessBatch = getBusinessBatch(term, latitude, longitude, offset)
            if (businessBatch.totalQueried <= 0) {
                emit(Resource.error(businessBatch, "No search results available for $term."))
            } else {
                emit(Resource.success(businessBatch))
            }
        } catch (exception: Exception) {
            // Instead of opting to handle every kind of exception, return a generic message.
            Log.e(TAG, "Error while requesting batch: ${exception.message}.")
            emit(Resource.error(null, "Uh oh! An error occurred while fetching the data."))
        }
    }

    /**
     * Make a request from the repository to GET business data and reviews and combine them into a
     * single batch object to be included in the view adapter.
     * @param term The query term to search.
     * @param latitude The latitude of the location to search.
     * @param longitude The longitude of the location to search.
     * @param offset The offset of the request for pagination.
     * @return A single [BusinessBatch] object that is a bi-function of all of the transactions.
     */
    @VisibleForTesting
    suspend fun getBusinessBatch(term: String, latitude: Double, longitude: Double, offset: Int): BusinessBatch {
        val businessesResponse = repository.getBusinesses(term, latitude, longitude, offset)
        val businessBody = businessesResponse.body()
        val businesses = businessBody?.businesses ?: listOf()
        val businessBatchList = businesses.flatMap { business ->
            val reviews = repository.getReviews(business.id)
                .body()?.reviews?: listOf()
            listOf(BusinessData(business, reviews))
        }
        return BusinessBatch(term, businessBody?.total ?: 0, businessBatchList)
    }
}