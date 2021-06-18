package com.natigbabayev.foursquare.search.domain

import com.natigbabayev.foursquare.search.domain.model.VenueSearchResult
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SearchVenuesUseCaseTest {
    @Suppress("PrivatePropertyName")
    private lateinit var SUT: SearchVenuesUseCase

    private val input = SearchVenuesUseCase.Input(
        keyword = "search word",
        latitude = 40.7243,
        longitude = -74.0018,
        page = 3
    )

    // region mocks
    private val mockVenueRepository: VenueRepository = mock()
    // endregion

    @Before
    fun setUp() {
        SUT = SearchVenuesUseCase(mockVenueRepository)
    }

    @Test
    fun invoke_callsVenueRepository() {
        // Arrange
        val venueSearchResult = VenueSearchResult(venues = emptyList(), totalResults = 0)

        whenever(mockVenueRepository.searchVenues(any(), any(), any(), any()))
            .thenReturn(Single.just(venueSearchResult))
        // Act
        SUT.invoke(input).test()
        // Assert
        // in this case, we'll skip 20 items to load 3rd page
        val expectedOffset = (input.page - 1) * VenueRepository.PAGE_SIZE
        verify(mockVenueRepository).searchVenues(
            keyword = input.keyword,
            latitude = input.latitude,
            longitude = input.longitude,
            offset = expectedOffset
        )
    }

    @Test
    fun invoke_whenNoMorePagesLeft_returnsOutputWithNullNextPage() {
        // Arrange
        val venueSearchResult = VenueSearchResult(
            venues = listOf(mock(), mock(), mock()),
            totalResults = 23 // 3 pages in total
        )

        whenever(mockVenueRepository.searchVenues(any(), any(), any(), any()))
            .thenReturn(Single.just(venueSearchResult))
        // Act
        val result = SUT.invoke(input).test()
        // Assert
        result
            .assertValue { it.nextPage == null }
            .assertValue { it.venues == venueSearchResult.venues }
    }

    @Test
    fun invoke_whenPagesLeft_returnsOutputWithNextPage() {
        // Arrange
        val venueSearchResult = VenueSearchResult(
            venues = listOf(mock(), mock(), mock()),
            totalResults = 33 // 4 pages in total
        )

        whenever(mockVenueRepository.searchVenues(any(), any(), any(), any()))
            .thenReturn(Single.just(venueSearchResult))
        // Act
        val result = SUT.invoke(input).test()
        // Assert
        result
            .assertValue { it.nextPage == input.page + 1 }
            .assertValue { it.venues == venueSearchResult.venues }
    }
}
