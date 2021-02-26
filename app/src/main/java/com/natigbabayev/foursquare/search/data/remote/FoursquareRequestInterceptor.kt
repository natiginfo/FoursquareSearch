package com.natigbabayev.foursquare.search.data.remote

import com.natigbabayev.foursquare.search.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class FoursquareRequestInterceptor(
    private val clientId: String = BuildConfig.API_CLIENT_ID,
    private val clientSecret: String = BuildConfig.API_CLIENT_SECRET,
    private val apiVersion: String = BuildConfig.API_VERSION
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url()
            .newBuilder()
            .addQueryParameter("client_id", clientId)
            .addQueryParameter("client_secret", clientSecret)
            .addQueryParameter("v", apiVersion)
            .build()

        val updatedRequest = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(updatedRequest)
    }
}
