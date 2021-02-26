package com.natigbabayev.foursquare.search.domain.model

data class Venue(
    val id: String,
    val name: String,
    val distance: Int,
    val formattedAddress: List<String>
)

data class VenueSearchResult(
    val venues: List<Venue>,
    val totalResults: Int
)
