package com.natigbabayev.foursquare.search.data

import com.natigbabayev.foursquare.search.data.remote.ExploreVenueResponse
import com.natigbabayev.foursquare.search.data.remote.FoursquareService
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VenueRepositoryImplTest {

    @Suppress("PrivatePropertyName")
    private lateinit var SUT: VenueRepositoryImpl

    // region mocks
    private val mockFoursquareService: FoursquareService = mock()
    // endregion

    @Before
    fun setUp() {
        SUT = VenueRepositoryImpl(mockFoursquareService)
    }

    @Test
    fun searchVenues_passesLongLatCorrectlyToTheService() {
        // Arrange
        val keyword = "search word"
        val latitude = 40.7243
        val longitude = -74.0018
        val offset = 0
        val exploreVenueResponse = ExploreVenueResponse(
            response = ExploreVenueResponse.Response(
                totalResults = 0,
                fullLocation = "",
                groups = emptyList()
            )
        )
        whenever(mockFoursquareService.getRecommendedVenues(any(), any(), any(), any()))
            .thenReturn(Single.just(exploreVenueResponse))
        // Act
        SUT.searchVenues(keyword, latitude, longitude, offset).test()
        // Assert
        verify(mockFoursquareService).getRecommendedVenues(
            keyword = keyword,
            location = "$latitude,$longitude",
            limit = VenueRepository.PAGE_SIZE,
            offset = offset
        )
    }
}
