package com.ngmatt.weedmapsandroidcodechallenge.ui.adapter

import android.Manifest
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ngmatt.weedmapsandroidcodechallenge.R
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.PERMISSION_REQUEST_CODE
import com.ngmatt.weedmapsandroidcodechallenge.data.model.Business
import com.ngmatt.weedmapsandroidcodechallenge.data.model.BusinessData
import com.ngmatt.weedmapsandroidcodechallenge.data.model.Review
import com.ngmatt.weedmapsandroidcodechallenge.utils.LocationUtils
import kotlinx.android.synthetic.main.list_item_business.view.*
import kotlinx.android.synthetic.main.list_item_business.view.textViewName
import kotlinx.android.synthetic.main.list_item_business.view.textViewRating
import kotlinx.android.synthetic.main.list_item_gps.view.*
import org.koin.dsl.module

@VisibleForTesting const val VIEW_TYPE_BUSINESS = 0
@VisibleForTesting const val VIEW_TYPE_LOADING = 1
@VisibleForTesting const val VIEW_TYPE_GPS_POSITION = 2

private const val TYPE_LOADING = "BUSINESS_ENTRY_LOADING"
private const val TYPE_GPS_POSITION = "BUSINESS_ENTRY_GPS_POSITION"

val adapterModule = module {
    factory { BusinessAdapter() }
}

/**
 * BusinessAdapter is an adapter class that holds a group of businesses from a business search
 * result.
 */
class BusinessAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val loadingData = BusinessData(Business(TYPE_LOADING), listOf(Review()))
    val gpsPositionData = BusinessData(Business(TYPE_GPS_POSITION), listOf(Review()))

    @VisibleForTesting val businesses = linkedMapOf<String, BusinessData>()

    private val entryTypeMap = hashMapOf(
        TYPE_LOADING to VIEW_TYPE_LOADING,
        TYPE_GPS_POSITION to VIEW_TYPE_GPS_POSITION
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_BUSINESS -> BusinessDataViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_business, parent, false))
            VIEW_TYPE_GPS_POSITION -> GpsPermissionDataViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_gps, parent, false))
            else -> BusinessDataViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_loading, parent, false))
        }
    }

    override fun getItemCount(): Int = businesses.size

    override fun getItemViewType(position: Int) =
        entryTypeMap[businesses.values.elementAt(position).business.id] ?: VIEW_TYPE_BUSINESS

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_BUSINESS -> (holder as BusinessDataViewHolder)
                .bind(businesses.values.elementAt(position))
            VIEW_TYPE_GPS_POSITION -> (holder as GpsPermissionDataViewHolder).bind()
        }
    }

    /**
     * Add businesses from a list to a linked map. This way we don't have to search the list for
     * the GPS or loading cards. This also helps if for any reason we get duplicate business IDs.
     * @param businessDataList The business data list to add to the business map.
     */
    fun addBusinesses(businessDataList: List<BusinessData>) {
        removeLoadingCard()
        businessDataList.forEach {
            businesses[it.business.id] = it
        }
    }

    /**
     * Clear the business map but keep the GPS card if it's meant to be there.
     */
    fun clearBusinesses() {
        when {
            businesses.containsKey(TYPE_GPS_POSITION) -> {
                businesses.clear()
                addBusinesses(listOf(gpsPositionData))
            }
            else -> businesses.clear()
        }
    }

    /**
     * Add the GPS card to the business map and update the data set.
     */
    fun addGpsCard() = addBusinesses(listOf(gpsPositionData))

    /**
     * Remove the GPS card from the business map and update the data set.
     */
    fun removeGpsCard() = businesses.remove(TYPE_GPS_POSITION)

    /**
     * Add the loading card to the business map and update the data set.
     */
    fun addLoadingCard() = addBusinesses(listOf(loadingData))

    /**
     * Remove the loading card from the business map and update the data set.
     */
    fun removeLoadingCard() = businesses.remove(TYPE_LOADING)

    /**
     * The BusinessDataViewHolder class holds all UI references for a business entry in the
     * RecyclerView.
     * @param itemView The single item view.
     */
    class BusinessDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(business: BusinessData) {
            itemView.apply {
                textViewName.text = business.business.name
                textViewRating.text = resources.getString(R.string.business_item_rating, business.business.rating)
                if (business.reviews.isNullOrEmpty()) {
                    textViewReview.text = resources.getString(R.string.business_review_none)
                } else {
                    textViewReview.text = resources.getString(R.string.business_review, business.reviews[0].text)
                }
                Glide.with(imageViewIcon.context)
                    .load(business.business.image_url)
                    .into(imageViewIcon)
            }
        }
    }

    /**
     * The GpsPermissionDataViewHolder class holds all UI references for a GPS entry in the
     * RecyclerView. It requires permission in order to launch a permission request.
     * @param itemView The single item view.
     */
    class GpsPermissionDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.apply {
                buttonEnableGps.setOnClickListener {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                }
            }
        }
    }
}