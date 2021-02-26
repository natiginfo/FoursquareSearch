package com.natigbabayev.foursquare.search.search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.observable
import com.jakewharton.rxrelay2.BehaviorRelay
import com.natigbabayev.foursquare.search.base.MvpPresenter
import com.natigbabayev.foursquare.search.di.SchedulerModule
import com.natigbabayev.foursquare.search.domain.SearchVenuesUseCase
import com.natigbabayev.foursquare.search.domain.model.LocationData
import com.natigbabayev.foursquare.search.domain.model.Venue
import com.natigbabayev.foursquare.search.domain.repository.LocationRepository
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SearchPresenter @Inject constructor(
    private val searchVenuesUseCase: SearchVenuesUseCase,
    private val locationRepository: LocationRepository,
    @Named(SchedulerModule.IO_SCHEDULER) private val ioScheduler: Scheduler,
    @Named(SchedulerModule.MAIN_SCHEDULER) private val mainScheduler: Scheduler
) : MvpPresenter<SearchContract.View>() {

    private val searchKeywordRelay = BehaviorRelay.create<String>()

    @VisibleForTesting
    var lastPagingData: PagingData<Venue>? = null

    private val compositeDisposable = CompositeDisposable()

    override fun getMainThreadScheduler(): Scheduler = mainScheduler

    fun updateSearchKeyword(keyword: String) {
        if (!locationRepository.hasLocationPermission()) {
            ifViewAttached { view -> view.onPermissionRequired() }
            return
        }
        searchKeywordRelay.accept(keyword)
    }

    @ExperimentalCoroutinesApi
    fun observeSearchResults() {
        if (lastPagingData != null) {
            ifViewAttached { view -> view.onVenuesLoaded(lastPagingData!!) }
            return
        }
        searchKeywordRelay
            .distinctUntilChanged { a, b -> a == b }
            .doOnNext {
                if (it.isBlank()) {
                    ifViewAttached { view -> view.onSearchWordEmpty() }
                } else {
                    ifViewAttached { view -> view.onSearchStarted() }
                }
            }
            .switchMap { keyword ->
                if (keyword.isBlank()) return@switchMap Observable.empty<PagingData<Venue>>()
                locationRepository.getLocation()
                    .flatMapObservable { locationOutput ->
                        createPagerObservable(keyword, locationOutput)
                    }
                    .subscribeOn(ioScheduler)
            }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onError = {
                    ifViewAttached { view ->
                        if (it is SecurityException) {
                            view.onPermissionRequired()
                        } else {
                            view.onGenericError()
                        }
                    }
                },
                onNext = {
                    lastPagingData = it
                    ifViewAttached { view -> view.onVenuesLoaded(it) }
                }
            )
            .addTo(compositeDisposable)
    }

    @ExperimentalCoroutinesApi
    private fun createPagerObservable(
        keyword: String,
        locationOutput: LocationData
    ): Observable<PagingData<Venue>> {
        val source = VenuesPagingSource(
            keyword = keyword,
            locationOutput = locationOutput,
            searchVenuesUseCase = searchVenuesUseCase
        )

        return Pager(PagingConfig(pageSize = VenueRepository.PAGE_SIZE)) { source }
            .observable
            .cachedIn(viewModelScope)
    }

    fun emptyAdapter() {
        ifViewAttached { it.onVenuesEmpty() }
    }

    fun loadingError(error: Throwable) {
        ifViewAttached { it.onNetworkError(error) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
