package com.ngmatt.weedmapsandroidcodechallenge.ui.custom

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.ngmatt.weedmapsandroidcodechallenge.R

/**
 * RecentSearchView is a custom view that will show all submitted queries as entries in the
 * suggestion adapter. It will order entries by the most recently submitted.
 */
class RecentSearchView : SearchView, SearchView.OnQueryTextListener,
    SearchView.OnSuggestionListener {

    var onSearchLambda: ((query: String) -> Unit) = {}
    var lastSearchedTerm: String? = null

    private val suggestions = linkedSetOf<String>()

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int)
            : super(context, attrs, defStyleAttr)

    init {
        setIconifiedByDefault(false)
        setOnQueryTextListener(this)
        setOnSuggestionListener(this)
        findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        suggestionsAdapter = SimpleCursorAdapter(context,
            R.layout.list_item_suggestion,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        // If we just searched this term, don't search again.
        if (query == null || query == lastSearchedTerm) return true
        lastSearchedTerm = query

        // Put the query term as the most recent suggestion, moving it to the top of the set, if it already existed in the set.
        if (suggestions.contains(query)) suggestions.remove(query)
        suggestions.add(query)

        updateSuggestions()

        // Inform the user that a new search has started.
        onSearchLambda(query)

        return false
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        return false
    }

    override fun onSuggestionClick(position: Int): Boolean {
        val cursor = suggestionsAdapter.getItem(position) as Cursor
        val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        setQuery(selection, false)
        return true
    }

    /**
     * Update the cursor to the current suggestions. Here we can opt to filter the suggestions, but
     * instead we'll show everything.
     */
    private fun updateSuggestions() {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
        suggestions.reversed().forEachIndexed { index, suggestion ->
            cursor.addRow(arrayOf(index, suggestion))
        }
        suggestionsAdapter.changeCursor(cursor)
    }

}