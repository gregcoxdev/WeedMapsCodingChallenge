package com.ngmatt.weedmapsandroidcodechallenge

import com.ngmatt.weedmapsandroidcodechallenge.data.model.Business
import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessResponse
import com.ngmatt.weedmapsandroidcodechallenge.data.model.Review
import com.ngmatt.weedmapsandroidcodechallenge.data.model.ReviewResponse
import retrofit2.Response

const val BUSINESS_SEARCH_TERM = "TERM"
const val BUSINESS_SEARCH_LATITUDE = 1.2345
const val BUSINESS_SEARCH_LONGITUDE = 2.3456
const val BUSINESS_SEARCH_OFFSET = 4
const val REVIEW_SEARCH_ID = "REVIEW_ID"

const val DEFAULT_BUSINESS_COUNT = 10
const val DEFAULT_BUSINESS_ID = "BUSINESS_ID"
const val DEFAULT_BUSINESS_NAME = "BUSINESS_NAME"
const val DEFAULT_BUSINESS_RATING = 5.0
const val DEFAULT_BUSINESS_IMAGE_URL = "BUSINESS_IMAGE_URL"
const val DEFAULT_REVIEW_DESCRIPTION = "REVIEW"

val defaultBusiness = Business(DEFAULT_BUSINESS_ID, DEFAULT_BUSINESS_NAME, DEFAULT_BUSINESS_RATING, DEFAULT_BUSINESS_IMAGE_URL)
val defaultBusinessResponse = BusinessResponse(DEFAULT_BUSINESS_COUNT, listOf(defaultBusiness))
val defaultResponseBusinessResponse: Response<BusinessResponse> = Response.success(
    defaultBusinessResponse
)
val defaultReview = Review(DEFAULT_REVIEW_DESCRIPTION)
val defaultReviewResponse = ReviewResponse(listOf(defaultReview))
val defaultResponseReviewResponse: Response<ReviewResponse> = Response.success(defaultReviewResponse)
