package com.natigbabayev.foursquare.search.search

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.natigbabayev.foursquare.search.domain.model.LocationData
import com.natigbabayev.foursquare.search.domain.SearchVenuesUseCase
import com.natigbabayev.foursquare.search.domain.model.Venue
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class VenuesPagingSource(
    private val keyword: String,
    private val locationOutput: LocationData,
    private val searchVenuesUseCase: SearchVenuesUseCase
) : RxPagingSource<Int, Venue>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Venue>> {
        val searchInput = SearchVenuesUseCase.Input(
            keyword = keyword,
            page = params.key ?: 1,
            latitude = locationOutput.latitude,
            longitude = locationOutput.longitude
        )
        return searchVenuesUseCase.invoke(searchInput)
            .subscribeOn(Schedulers.io())
            .map<LoadResult<Int, Venue>> { result ->
                LoadResult.Page(
                    data = result.venues,
                    prevKey = null,
                    nextKey = result.nextPage
                )
            }
            .onErrorReturn { e ->
                when (e) {
                    // Retrofit calls that return the body type throw either IOException for
                    // network failures, or HttpException for any non-2xx HTTP status codes.
                    // This code reports all errors to the UI, but you can inspect/wrap the
                    // exceptions to provide more context.
                    is IOException -> LoadResult.Error(e)
                    is HttpException -> LoadResult.Error(e)
                    else -> throw e
                }
            }
    }

    override fun getRefreshKey(state: PagingState<Int, Venue>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.nextKey }
    }
}
