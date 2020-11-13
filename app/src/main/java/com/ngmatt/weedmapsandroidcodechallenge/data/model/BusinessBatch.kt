package com.ngmatt.weedmapsandroidcodechallenge.data.model

/**
 * BusinessBatch is a data class that holds the batch of business requests. [BusinessData] holds one
 * transaction but this class will hold several [BusinessData] transactions.
 */
data class BusinessBatch(
    val term: String,
    val totalQueried: Int,
    val businessData: List<BusinessData>
)