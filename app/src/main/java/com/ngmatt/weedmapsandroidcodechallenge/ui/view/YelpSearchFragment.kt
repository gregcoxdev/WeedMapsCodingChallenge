package com.ngmatt.weedmapsandroidcodechallenge.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngmatt.weedmapsandroidcodechallenge.R
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LATITUDE
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LONGITUDE
import com.ngmatt.weedmapsandroidcodechallenge.data.model.*
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.BusinessAdapter
import com.ngmatt.weedmapsandroidcodechallenge.ui.viewmodel.YelpSearchViewModel
import com.ngmatt.weedmapsandroidcodechallenge.utils.LocationUtils
import kotlinx.android.synthetic.main.fragment_yelp_search.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "YelpSearchFragment"

/**
 * YelpSearchFragment is a fragment used to conduct business search queries from the Yelp API. It
 * will allow the user to continually scroll through businesses in a preset area or from the user's
 * location.
 */
class YelpSearchFragment : Fragment() {

    @VisibleForTesting
    val businessAdapter: BusinessAdapter by inject()

    private val viewModel: YelpSearchViewModel by viewModel()
    private var searchOffset = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_yelp_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        if (!LocationUtils.isLocationPermissionGranted(context as Activity)) {
            showGpsCard()
        }
        LocationUtils.startLocationUpdates(context as Activity)
    }

    override fun onPause() {
        super.onPause()
        LocationUtils.stopLocationUpdates(context as Activity)
    }

    /**
     * Hide the GPS card in the recycler view.
     */
    fun hideGpsCard() {
        businessAdapter.apply {
            removeGpsCard()
            notifyDataSetChanged()
        }
    }

    /**
     * Show the GPS card in the recycler view.
     */
    private fun showGpsCard() {
        businessAdapter.apply {
            addGpsCard()
            notifyDataSetChanged()
        }
    }

    /**
     * Initialize the UI components and their respective listeners.
     */
    private fun setupUI() {
        initRecyclerView()
        initSearchView()
        startLoadingAnimation()
    }

    /**
     * Initialize the recycler view components and it's action when requesting more data.
     */
    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = businessAdapter
            onRequestMoreDataLambda = { itemCount ->
                // Offset the request by the number of items in the adapter and search.
                searchOffset = itemCount
                searchBar.lastSearchedTerm?.let {
                    searchBusinesses(it, offset = searchOffset)
                }
            }
        }
    }


    /**
     * Initialize the search view components and action when a new query is submitted.
     */
    @SuppressLint("MissingPermission")
    private fun initSearchView() {
        searchBar.apply {
            onSearchLambda = { queryTerm ->

                // Change the text but wait to make it visible at the same time as the recycler.
                Log.i(TAG, "Searching for results for $queryTerm.")
                textViewSearched.visibility = View.GONE
                textViewSearched.text = resources.getString(R.string.results_for_search, queryTerm)

                // Display the loading animation while searching.
                startLoadingAnimation()

                // Begin searching and reset the pagination offset.
                businessAdapter.apply {
                    clearBusinesses()
                    notifyDataSetChanged()
                }
                searchOffset = 0

                // If permission is still granted before trying to grab it, if it fails, use the default.
                if (LocationUtils.isLocationPermissionGranted(context)) {
                    // Pull location and send that with the request.
                    val userLocation = LocationUtils.getLastKnownLocation(context)
                    Log.i(TAG, "Searching businesses near $userLocation.")
                    if (userLocation == null)
                        searchBusinesses(
                            queryTerm,
                            offset = searchOffset)
                    else
                        searchBusinesses(
                            queryTerm,
                            userLocation.first,
                            userLocation.second,
                            searchOffset)
                } else {
                    searchBusinesses(queryTerm, offset = searchOffset)
                }
            }
        }
    }

    /**
     * Update the frame data for the progress bar and begin animation. When finished with the
     * success or failure animation, execute the lambda.
     */
    private fun startLoadingAnimation() {
        lottieProgressLoader.apply {
            startLoadingAnimation()
            onAnimationSuccessEndLambda = {
                textViewSearched.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                lottieProgressLoader.visibility = View.GONE
            }
            onAnimationFailureEndLambda = {
                textViewSearched.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                lottieProgressLoader.visibility = View.GONE
            }
        }
    }

    /**
     * Search for a business or businesses from Yelp with the given parameters.
     * @param term The query term to search.
     * @param latitude The latitude of the location to search.
     * @param longitude The longitude of the location to search.
     * @param offset The offset of the request for pagination.
     * @return The LiveData to observe for this transaction.
     */
    private fun searchBusinesses(term: String, latitude: Double = DEFAULT_LATITUDE,
                                 longitude: Double = DEFAULT_LONGITUDE, offset: Int) =
        viewModel.searchForBusinesses(term, latitude, longitude, offset).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.searchStatus) {
                    SUCCESS -> handleSuccessRequest(resource)
                    ERROR -> handleErrorRequest(resource)
                    LOADING -> handleLoadingRequest()
                }
            }
        })

    /**
     * Handle the status update when a request is successful. Generally, this means updating the UI
     * without need for user interaction.
     * @param resource The result containing information about the success.
     */
    private fun handleSuccessRequest(resource: Resource<BusinessBatch>) {
        resource.data?.let { businessBatch ->
            // Update the businesses in the adapter.
            if (searchBar.lastSearchedTerm == businessBatch.term) {
                addBatch(businessBatch)
                Log.i(TAG, "Adapter contains ${businessAdapter.itemCount} out of " +
                        "${businessBatch.totalQueried} upstream items.")
            }

            // Load is complete, show the success animation and stop blocking new load requests.
            lottieProgressLoader.startSuccessfulAnimation(false)
            recyclerView.debounceRequests = false
        }
    }

    /**
     * Handle the status update when a request fails. Generally, this means showing an error and
     * prompting the user to do something about it.
     * @param resource The result containing information about the failure.
     */
    private fun handleErrorRequest(resource: Resource<BusinessBatch>) {
        textViewSearched.text = resource.message
        if (businessAdapter.itemCount <= 0) {
            lottieProgressLoader.startFailureAnimation(false)
        } else {
            businessAdapter.apply {
                removeLoadingCard()
                notifyDataSetChanged()
            }
        }
        recyclerView.debounceRequests = false
    }

    /**
     * Handle the status update while a request is being loaded. Generally, this means showing
     * something like a progress bar to the user.
     */
    private fun handleLoadingRequest() {
        if (businessAdapter.itemCount <= 0) {
            // Show the large progress bar since there are no recycler items.
            lottieProgressLoader.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // Add a loading bar to the recycler and debounce scrolling requests.
            businessAdapter.apply {
                addLoadingCard()
                notifyDataSetChanged()
            }
            recyclerView.debounceRequests = true
        }
    }

    /**
     * Add a batch of business data into the business adapter.
     * @param businessBatch A batch of multiple business data to be added to the adapter.
     */
    private fun addBatch(businessBatch: BusinessBatch) {
        businessAdapter.apply {
            addBusinesses(businessBatch.businessData)
            notifyDataSetChanged()
        }
    }
}
