package com.natigbabayev.foursquare.search.search

import com.natigbabayev.foursquare.search.domain.SearchVenuesUseCase
import com.natigbabayev.foursquare.search.domain.model.LocationData
import com.natigbabayev.foursquare.search.domain.repository.LocationRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.times
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


class SearchPresenterTest {
    @Suppress("PrivatePropertyName")
    private lateinit var SUT: SearchPresenter

    private val testScheduler = TestScheduler()

    // paging library uses coroutines under the hood
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    // region mocks
    private val mockLocationRepository: LocationRepository = mock()
    private val mockSearchVenuesUseCase: SearchVenuesUseCase = mock()
    private val mockView: SearchContract.View = mock()
    // endregion

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        SUT = SearchPresenter(
            searchVenuesUseCase = mockSearchVenuesUseCase,
            locationRepository = mockLocationRepository,
            ioScheduler = testScheduler,
            mainScheduler = testScheduler
        )

        // paging library uses coroutines under the hood
        Dispatchers.setMain(testDispatcher)

        SUT.attachView(mockView)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        // Resets state of the [Dispatchers.Main] to the original main dispatcher.
        // For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun updateSearchKeyword_whenUserDoesNotHaveLocationPermission_callsOnPermissionRequired() {
        // Arrange
        val inputWord = "test word"
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(false)
        // Act
        SUT.updateSearchKeyword(inputWord)
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onPermissionRequired()
        verify(mockView, times(0)).onSearchStarted()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenLastPagingDataIsNotNull_callsOnVenuesLoaded() {
        // Arrange
        SUT.lastPagingData = mock()
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        // Act
        SUT.observeSearchResults()
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onVenuesLoaded(SUT.lastPagingData!!)
        verify(mockView, times(0)).onPermissionRequired()
        verify(mockView, times(0)).onSearchStarted()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenSearchWordIsEmpty_callsOnSearchWordEmpty() {
        // Arrange
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword("")
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onSearchWordEmpty()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenSearchWordIsNotEmpty_callsOnSearchStarted() {
        // Arrange
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        whenever(mockSearchVenuesUseCase.invoke(any())).thenReturn(Single.just(mock()))
        whenever(mockLocationRepository.getLocation()).thenReturn(Single.just(mock()))
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword("non-empty")
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onSearchStarted()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenSearchWordIsEmpty_doesNotCallLocationRepository() {
        // Arrange
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        whenever(mockSearchVenuesUseCase.invoke(any())).thenReturn(Single.just(mock()))
        whenever(mockLocationRepository.getLocation()).thenReturn(Single.just(mock()))
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword("")
        testScheduler.triggerActions()
        // Assert
        verify(mockLocationRepository, times(0)).getLocation()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenSecurityExceptionThrown_callsOnPermissionRequired() {
        // Arrange
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        whenever(mockSearchVenuesUseCase.invoke(any())).thenReturn(Single.just(mock()))
        whenever(mockLocationRepository.getLocation()).thenThrow(SecurityException())
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword("test")
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onSearchStarted()
        verify(mockView).onPermissionRequired()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenSomeOtherExceptionThrown_callsOnGenericError() {
        // Arrange
        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)
        whenever(mockSearchVenuesUseCase.invoke(any())).thenReturn(Single.just(mock()))
        whenever(mockLocationRepository.getLocation()).thenThrow(RuntimeException())
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword("test")
        testScheduler.triggerActions()
        // Assert
        verify(mockView).onSearchStarted()
        verify(mockView).onGenericError()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchResults_whenPagingSucceeds_callsOnVenuesLoaded() {
        // Arrange
        val locationData = LocationData(latitude = 40.7243, longitude = -74.0018)
        val keyword = "test"

        whenever(mockLocationRepository.hasLocationPermission()).thenReturn(true)

        val output = SearchVenuesUseCase.Output(venues = emptyList(), nextPage = null)
        whenever(mockSearchVenuesUseCase.invoke(any())).thenReturn(Single.just(output))

        whenever(mockLocationRepository.getLocation()).thenReturn(Single.just(locationData))
        // Act
        SUT.observeSearchResults()
        SUT.updateSearchKeyword(keyword)
        testScheduler.triggerActions()
        // Assert
        // verify loading state
        verify(mockView).onSearchStarted()

        // verify that view is being updated with the result
        verify(mockView).onVenuesLoaded(any())
    }
}
