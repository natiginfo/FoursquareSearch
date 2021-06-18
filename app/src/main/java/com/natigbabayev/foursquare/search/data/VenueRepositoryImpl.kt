package com.natigbabayev.foursquare.search.data

import com.natigbabayev.foursquare.search.data.mapper.toVenue
import com.natigbabayev.foursquare.search.data.remote.FoursquareService
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import com.natigbabayev.foursquare.search.domain.model.VenueSearchResult
import io.reactivex.Single
import javax.inject.Inject

class VenueRepositoryImpl @Inject constructor(
    private val service: FoursquareService
) : VenueRepository {

    override fun searchVenues(
        keyword: String,
        latitude: Double,
        longitude: Double,
        offset: Int
    ): Single<VenueSearchResult> {
        return service.getRecommendedVenues(
            keyword = keyword,
            location = "$latitude,$longitude",
            limit = VenueRepository.PAGE_SIZE,
            offset = offset
        )
            .map { networkResponse ->
                val venues = networkResponse.response.groups
                    .map { it.items }
                    .flatten()
                    .map { it.venue.toVenue() }

                VenueSearchResult(
                    venues = venues,
                    totalResults = networkResponse.response.totalResults
                )
            }
    }
}
