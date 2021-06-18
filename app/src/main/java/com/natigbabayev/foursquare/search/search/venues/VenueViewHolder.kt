package com.natigbabayev.foursquare.search.search.venues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.natigbabayev.foursquare.search.R
import com.natigbabayev.foursquare.search.databinding.ItemVenueBinding
import com.natigbabayev.foursquare.search.domain.model.Venue

class VenueViewHolder(
    private val binding: ItemVenueBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): VenueViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return VenueViewHolder(ItemVenueBinding.inflate(inflater, parent, false))
        }
    }

    fun bind(venue: Venue) {
        with(binding) {
            textName.text = venue.name
            textAddress.text = venue.formattedAddress.joinToString(separator = "\n")
            // Refactor to use some resource provider in future
            val context = binding.root.context
            textDistance.text = context.getString(
                R.string.distance_text, (venue.distance / 1000.0f)
            )
        }
    }
}
