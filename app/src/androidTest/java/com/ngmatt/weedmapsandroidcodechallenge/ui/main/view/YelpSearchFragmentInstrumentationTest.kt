package com.ngmatt.weedmapsandroidcodechallenge.ui.main.view

import android.app.SearchManager
import android.database.Cursor
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ngmatt.weedmapsandroidcodechallenge.InstrumentationUtils
import com.ngmatt.weedmapsandroidcodechallenge.R
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_NETWORK_TIMEOUT
import com.ngmatt.weedmapsandroidcodechallenge.typeSearchViewText
import com.ngmatt.weedmapsandroidcodechallenge.ui.view.YelpSearchFragment
import kotlinx.android.synthetic.main.fragment_yelp_search.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val INPUT_SEVERAL_RESULTS = "Food"
private const val INPUT_ZERO_RESULTS = ";QWEASDZXCPOILKJMNB!;"

@RunWith(AndroidJUnit4::class)
class YelpSearchFragmentInstrumentationTest {

    private lateinit var fragment: YelpSearchFragment

    @Before
    fun setup() {
        fragment = InstrumentationUtils.launchFragment()
    }

    @Test
    fun testSearchBarSuggestions() {

        // Verify that the searched element was added to the suggestions.
        onView(withId(R.id.searchBar)).perform(ViewActions.click())
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(INPUT_SEVERAL_RESULTS, true))
        var firstCursor = fragment.searchBar.suggestionsAdapter.getItem(0) as Cursor
        var firstSelection = firstCursor.getString(firstCursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        assertEquals(INPUT_SEVERAL_RESULTS, firstSelection)
        waitForNetwork()

        // Verify that the newest searched was added to the front of the suggestions.
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(INPUT_ZERO_RESULTS, true))
        firstCursor = fragment.searchBar.suggestionsAdapter.getItem(0) as Cursor
        firstSelection = firstCursor.getString(firstCursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        assertEquals(INPUT_ZERO_RESULTS, firstSelection)

    }

    @Test
    fun testSearchYieldsResults() {
        onView(withId(R.id.searchBar)).perform(ViewActions.click())
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(INPUT_SEVERAL_RESULTS, true))
        waitForNetwork()
        onView(withId(R.id.textViewSearched)).check(matches(withText(fragment.resources.getString(R.string.results_for_search, INPUT_SEVERAL_RESULTS))))
        assertEquals(View.VISIBLE, fragment.recyclerView.visibility)
        assertEquals(fragment.businessAdapter.businesses.size, fragment.businessAdapter.itemCount)
    }

    @Test
    fun testSearchYieldsNoResults() {
        onView(withId(R.id.searchBar)).perform(ViewActions.click())
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(INPUT_ZERO_RESULTS, true))
        waitForNetwork()
        println(fragment.textViewSearched.text)
        onView(withId(R.id.textViewSearched)).check(matches(withText("No search results available for $INPUT_ZERO_RESULTS.")))
        assertEquals(View.VISIBLE, fragment.recyclerView.visibility)
        assertEquals(0, fragment.businessAdapter.itemCount)
    }

    @Test
    fun testRecyclerViewInfiniteScrolling() {

        // Request focus and enter input, then wait for the data to load.
        onView(withId(R.id.searchBar)).perform(ViewActions.click())
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(INPUT_SEVERAL_RESULTS, true))
        waitForNetwork()
        val itemCountAfterFirstLoad = fragment.businessAdapter.itemCount

        // Assert that the result text shows the element was found.
        onView(withId(R.id.textViewSearched)).check(matches(withText(fragment.resources.getString(R.string.results_for_search, INPUT_SEVERAL_RESULTS))))
        fragment.recyclerView.adapter?.let {
            if (it.itemCount > 0) {
                onView(withId(R.id.recyclerView)).perform(
                    RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                        it.itemCount - 1
                    )
                )
            }
        }
        waitForNetwork()
        assertNotEquals(itemCountAfterFirstLoad, fragment.businessAdapter.itemCount)

    }

    /**
     * Wait for a network response. By default, we'll wait the default timeout time in order to make
     * sure we capture any kind of event that can occur.
     */
    private fun waitForNetwork() {
        Thread.sleep(DEFAULT_NETWORK_TIMEOUT)
    }

}