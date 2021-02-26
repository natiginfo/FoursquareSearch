package com.natigbabayev.foursquare.search.search.venues

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.natigbabayev.foursquare.search.domain.model.Venue

class VenueAdapter : PagingDataAdapter<Venue, VenueViewHolder>(VenueDiff()) {
    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        return VenueViewHolder.from(parent)
    }
}
