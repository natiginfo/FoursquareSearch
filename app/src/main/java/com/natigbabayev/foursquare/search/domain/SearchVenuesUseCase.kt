package com.natigbabayev.foursquare.search.domain

import com.natigbabayev.foursquare.search.domain.model.Venue
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.ceil

class SearchVenuesUseCase @Inject constructor(
    private val venueRepository: VenueRepository
) {
    data class Input(
        val keyword: String,
        val page: Int,
        val longitude: Double,
        val latitude: Double
    )

    data class Output(
        val venues: List<Venue>,
        val nextPage: Int?
    )

    fun invoke(input: Input): Single<Output> {
        return venueRepository.searchVenues(
            keyword = input.keyword,
            longitude = input.longitude,
            latitude = input.latitude,
            offset = (input.page - 1) * VenueRepository.PAGE_SIZE
        )
            .map {
                val totalPageCount = ceil(
                    it.totalResults.toDouble() / VenueRepository.PAGE_SIZE
                ).toInt()

                val pagesLeft = totalPageCount - input.page

                Output(
                    nextPage = if (pagesLeft > 0) input.page + 1 else null,
                    venues = it.venues
                )
            }
    }
}
