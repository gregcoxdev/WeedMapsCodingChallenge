package com.ngmatt.weedmapsandroidcodechallenge.data.model

/**
 * BusinessResponse is the data class for the business search that the JSON is parsed in to.
 * @param total The total of businesses found by the search.
 * @param businesses The list of businesses reported by the query. This is limited by the search but
 * also capped at 50.
 */
data class BusinessResponse(
    val total: Int,
    val businesses: List<Business>
)