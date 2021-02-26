package com.natigbabayev.foursquare.search.search.venues


import androidx.recyclerview.widget.DiffUtil
import com.natigbabayev.foursquare.search.domain.model.Venue

class VenueDiff : DiffUtil.ItemCallback<Venue>() {
    override fun areItemsTheSame(oldItem: Venue, newItem: Venue): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Venue, newItem: Venue): Boolean {
        return oldItem == newItem
    }
}
