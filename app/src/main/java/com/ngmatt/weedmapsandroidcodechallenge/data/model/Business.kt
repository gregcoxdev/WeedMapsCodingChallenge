package com.ngmatt.weedmapsandroidcodechallenge.data.model

/**
 * Business is the data class for each individual Business object that the JSON is parsed in to.
 * @param id The identifier for the business.
 * @param name The name of the business.
 * @param rating The rating of the business out of 5.
 * @param image_url The URL to the profile picture of the business.
 */
data class Business(
    val id: String,
    val name: String = "",
    val rating: Double = 0.0,
    val image_url: String = ""
)