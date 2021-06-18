package com.natigbabayev.foursquare.search.data.remote

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

class FoursquareRequestInterceptorTest {

    private val apiVersion = "apiVersion"
    private val clientId = "clientId"
    private val clientSecret = "clientSecret"

    @Suppress("PrivatePropertyName")
    private val SUT: FoursquareRequestInterceptor = FoursquareRequestInterceptor(
        clientId = clientId,
        clientSecret = clientSecret,
        apiVersion = apiVersion
    )

    private lateinit var mockWebServer: MockWebServer

    private lateinit var client: OkHttpClient

    private lateinit var retrofit: Retrofit

    @Before
    fun setup() {
        client = OkHttpClient.Builder()
            .addInterceptor(SUT)
            .build()

        mockWebServer = MockWebServer()

        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun interceptorModifiesRequestUrl() {
        // Arrange
        val api = retrofit.create(TestApi::class.java)
        val successResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(successResponse)
        // Act
        api.test().execute()
        // Assert
        val request = mockWebServer.takeRequest()
        val url = request.requestUrl.encodedQuery()
        check(url != null)
        assertTrue(url.contains("client_id=$clientId"))
        assertTrue(url.contains("client_secret=$clientSecret"))
        assertTrue(url.contains("v=$apiVersion"))
    }
}

private interface TestApi {
    @GET("/test")
    fun test(): Call<Void>
}
