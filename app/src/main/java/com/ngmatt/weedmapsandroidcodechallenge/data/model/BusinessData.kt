package com.ngmatt.weedmapsandroidcodechallenge.data.model

/**
 * BusinessData is a bi-function data class of the [Business] and [Review] classes. These classes
 * are joined when the network request is complete.
 * @param business The business data.
 * @param reviews The reviews for [business].
 */
data class BusinessData(
    val business: Business,
    val reviews: List<Review>
)