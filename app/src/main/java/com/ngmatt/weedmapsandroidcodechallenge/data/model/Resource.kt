package com.ngmatt.weedmapsandroidcodechallenge.data.model

import androidx.annotation.IntDef

const val SUCCESS = 0
const val ERROR = 1
const val LOADING = 2

@IntDef(
    SUCCESS,
    ERROR,
    LOADING
)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchStatus

/**
 * Resource is a data class that houses data emitted from the view model to the view. Each state
 * indicates a step in the network process.
 * @param searchStatus The status of the action.
 * @param data The data included.
 * @param message The message, usually housing an error.
 */
data class Resource<out T>(val searchStatus: @SearchStatus Int, val data: T?,
                           val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(SUCCESS, data, null)
        fun <T> error(data: T?, message: String): Resource<T> = Resource(ERROR, data, message)
        fun <T> loading(data: T?): Resource<T> = Resource(LOADING, data, null)
    }
}