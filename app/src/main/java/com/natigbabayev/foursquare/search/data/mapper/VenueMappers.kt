package com.natigbabayev.foursquare.search.data.mapper

import com.natigbabayev.foursquare.search.data.remote.ExploreVenueResponse
import com.natigbabayev.foursquare.search.domain.model.Venue

fun ExploreVenueResponse.Venue.toVenue(): Venue {
    return Venue(
        id = id,
        name = name,
        distance = location.distance,
        formattedAddress = location.formattedAddress
    )
}
