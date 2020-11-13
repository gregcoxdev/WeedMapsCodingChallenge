package com.ngmatt.weedmapsandroidcodechallenge.data.model

/**
 * ReviewResponse is the data class for the review search that the JSON is parsed in to.
 * @param reviews The list of reviews reported by the query. This will return a maximum of 3
 * reviews per business.
 */
data class ReviewResponse(
    val reviews: List<Review>
)