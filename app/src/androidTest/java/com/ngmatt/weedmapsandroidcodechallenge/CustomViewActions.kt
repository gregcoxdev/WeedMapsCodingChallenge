package com.ngmatt.weedmapsandroidcodechallenge

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf

/**
 * A custom view action to input text into a search view. This is needed because typeText only
 * supports TextViews.
 * @param text The text to enter.
 */
fun typeSearchViewText(text: String, submit: Boolean): ViewAction {
    return object : ViewAction {
        override fun getDescription(): String {
            return "Change the query text for a search view."
        }

        override fun getConstraints(): Matcher<View> {
            return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
        }

        override fun perform(uiController: UiController?, view: View?) {
            (view as SearchView).setQuery(text, submit)
        }
    }
}