package com.ngmatt.weedmapsandroidcodechallenge.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "InfiniteRecyclerView"

/**
 * InfiniteRecyclerView is a custom view class that will perform a lambda operation every time the
 * user scrolls to the very bottom of the recycler view. It also allows for debouncing the requests
 * in order to make sure there isn't an overflow of network requests.
 */
class InfiniteRecyclerView : RecyclerView {

    var onRequestMoreDataLambda: ((itemCount: Int) -> Unit) = {}
    var debounceRequests = false
    var lastVisibleItemPosition = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Debounce the requests so the user doesn't infinitely scroll through the recycler.
                if (debounceRequests) {
                    return
                }

                // Find the last position visible.
                val layoutManager =
                    recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                // Filter out multiple requests as the user scrolls.
                if (lastVisiblePosition == lastVisibleItemPosition)
                    return
                else
                    lastVisibleItemPosition = lastVisiblePosition

                // If the last item is visible, we should request more data.
                adapter?.let { nonNullAdapter ->
                    if (lastVisiblePosition == nonNullAdapter.itemCount - 1) {
                        Log.i(TAG, "Requesting more data.")
                        onRequestMoreDataLambda(nonNullAdapter.itemCount)
                    }
                }
            }
        })
    }

}