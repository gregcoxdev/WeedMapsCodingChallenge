package com.ngmatt.weedmapsandroidcodechallenge

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle

object InstrumentationUtils {

    /**
     * Start a fragment for testing.
     * @param fragmentArgs The fragment arguments to use when launching the fragment.
     * @return A [T] fragment.
     */
    internal inline fun <reified T: Fragment> launchFragment(fragmentArgs: Bundle = Bundle()): T =
        internalLaunchFragment<T>(fragmentArgs) ?: throw IllegalStateException("The fragment specified could not be started.")

    private inline fun <reified T: Fragment> internalLaunchFragment(fragmentArgs: Bundle): T? {

        // Start the fragment.
        val scenario = launchFragmentInContainer<T>(fragmentArgs, themeResId = R.style.AppTheme)
        var tFragment: T? = null
        scenario.onFragment { fragment ->
            tFragment = fragment
        }.moveToState(Lifecycle.State.RESUMED)

        return tFragment
    }

}