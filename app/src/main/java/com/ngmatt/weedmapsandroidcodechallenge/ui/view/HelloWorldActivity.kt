package com.ngmatt.weedmapsandroidcodechallenge.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.ngmatt.weedmapsandroidcodechallenge.R
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.PERMISSION_REQUEST_CODE
import com.ngmatt.weedmapsandroidcodechallenge.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_hello_world.*
import org.koin.android.ext.android.inject

/**
 * HelloWorldActivity is a class that houses activity level responsibilities. For this example, it
 * will only house a single fragment, but could be used to house several more.
 */
class HelloWorldActivity : AppCompatActivity() {

    private val yelpSearchFragment: YelpSearchFragment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_world)
        supportFragmentManager.beginTransaction().replace(R.id.root, yelpSearchFragment).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    yelpSearchFragment.hideLocationCard()
                    LocationUtils.startLocationUpdates(this)
                } else {
                    // If the user really doesn't want to enable permission, hide the option.
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(root, R.string.location_permission_denied, Snackbar.LENGTH_LONG).setAction("OK", {}).show()
                    }
                }
                return
            }
        }

    }

}