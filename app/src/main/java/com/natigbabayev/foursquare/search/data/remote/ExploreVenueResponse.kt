package com.natigbabayev.foursquare.search.data.remote

import com.squareup.moshi.Json

data class ExploreVenueResponse(
    @Json(name = "response") val response: Response
) {
    data class Response(
        @Json(name = "totalResults") val totalResults: Int,
        @Json(name = "headerFullLocation") val fullLocation: String,
        @Json(name = "groups") val groups: List<Group>
    )

    data class Group(
        @Json(name = "type") val type: String,
        @Json(name = "name") val name: String,
        @Json(name = "items") val items: List<GroupItem>
    )

    data class GroupItem(
        @Json(name = "venue") val venue: Venue
    )

    data class Venue(
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "location") val location: Location
    )

    data class Location(
        @Json(name = "distance") val distance: Int,
        @Json(name = "formattedAddress") val formattedAddress: List<String>
    )
}
