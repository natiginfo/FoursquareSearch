package com.natigbabayev.foursquare.search.data.remote

import com.natigbabayev.foursquare.search.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class FoursquareRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url()
            .newBuilder()
            .addQueryParameter("client_id", BuildConfig.API_CLIENT_ID)
            .addQueryParameter("client_secret", BuildConfig.API_CLIENT_SECRET)
            .addQueryParameter("v", BuildConfig.API_VERSION)
            .build()

        val updatedRequest = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(updatedRequest)
    }
}
