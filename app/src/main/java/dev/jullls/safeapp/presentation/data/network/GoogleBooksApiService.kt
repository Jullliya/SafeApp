package dev.jullls.safeapp.presentation.data.network

import dev.jullls.safeapp.presentation.data.response.GoogleBooksResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0
    ): GoogleBooksResponse

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"

        fun create(): GoogleBooksApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GoogleBooksApiService::class.java)
        }
    }
}