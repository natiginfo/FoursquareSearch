package com.natigbabayev.foursquare.search.data.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FoursquareService {
    companion object {
        const val API_BASE_URL = "https://api.foursquare.com/v2/"
    }

    @GET("venues/explore")
    fun getRecommendedVenues(
        @Query("query") keyword: String,
        @Query("ll") location: String,
        @Query("limit") limit: Int, // used for pagination
        @Query("offset") offset: Int, // used for pagination
    ): Single<ExploreVenueResponse>
}
