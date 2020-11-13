package com.ngmatt.weedmapsandroidcodechallenge.ui.main.adapter

import com.ngmatt.weedmapsandroidcodechallenge.defaultBusiness
import com.ngmatt.weedmapsandroidcodechallenge.defaultReview
import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessData
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.BusinessAdapter
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.VIEW_TYPE_BUSINESS
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.VIEW_TYPE_GPS_POSITION
import com.ngmatt.weedmapsandroidcodechallenge.ui.adapter.VIEW_TYPE_LOADING
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BusinessAdapterTest {

    private val adapter = BusinessAdapter()

    @Before
    fun setUp() {
        assertEquals(0, adapter.itemCount)
        adapter.addBusinesses(listOf(BusinessData(defaultBusiness, listOf(defaultReview))))
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun testClearBusinesses() {
        adapter.clearBusinesses()
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun testClearBusinessesWithGps() {
        adapter.addGpsCard()
        adapter.clearBusinesses()
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun testAddGpsCard() {
        adapter.addGpsCard()
        assertEquals(2, adapter.itemCount)
        assertTrue(adapter.businesses.contains(adapter.gpsPositionData.business.id))
    }

    @Test
    fun testRemoveGpsCard() {
        adapter.removeGpsCard()
        assertEquals(1, adapter.itemCount)
        assertTrue(!adapter.businesses.contains(adapter.gpsPositionData.business.id))
    }

    @Test
    fun testAddLoadingCard() {
        adapter.addLoadingCard()
        assertEquals(2, adapter.itemCount)
        assertTrue(adapter.businesses.contains(adapter.loadingData.business.id))
    }

    @Test
    fun testRemoveLoadingCard() {
        adapter.removeLoadingCard()
        assertEquals(1, adapter.itemCount)
        assertTrue(!adapter.businesses.contains(adapter.loadingData.business.id))
    }

    @Test
    fun testGetItemViewType() {
        adapter.addGpsCard()
        adapter.addLoadingCard()
        assertEquals(adapter.getItemViewType(0), VIEW_TYPE_BUSINESS)
        assertEquals(adapter.getItemViewType(1), VIEW_TYPE_GPS_POSITION)
        assertEquals(adapter.getItemViewType(2), VIEW_TYPE_LOADING)
    }

}