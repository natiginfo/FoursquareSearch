package com.natigbabayev.foursquare.search.domain.repository

import com.natigbabayev.foursquare.search.domain.model.VenueSearchResult
import io.reactivex.Single

interface VenueRepository {

    companion object {
        const val PAGE_SIZE = 10
    }

    fun searchVenues(
        keyword: String,
        latitude: Double,
        longitude: Double,
        offset: Int
    ): Single<VenueSearchResult>
}
