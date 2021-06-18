package com.natigbabayev.foursquare.search.search

import androidx.paging.PagingData
import com.natigbabayev.foursquare.search.base.MvpView
import com.natigbabayev.foursquare.search.domain.model.Venue

interface SearchContract {
    interface View : MvpView {
        fun onVenuesLoaded(venues: PagingData<Venue>)
        fun onSearchWordEmpty()
        fun onSearchStarted()
        fun onVenuesEmpty()
        fun onGenericError()
        fun onNetworkError(error: Throwable)
        fun onPermissionRequired()
    }
}
