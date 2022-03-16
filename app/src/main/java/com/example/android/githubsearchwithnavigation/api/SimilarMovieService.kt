package com.example.android.githubsearchwithnavigation.api

import com.example.android.githubsearchwithnavigation.data.MovieSearchResults
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Path

interface SimilarMovieService {
    @GET("3/search/movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movie_id: String,
        @Query("api_key") key: String,
        @Query("include_adult") include_adult: String
    ) : MovieSearchResults

    //Changing this file to query for a specific movie
    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/"
        fun create() : SimilarMovieService {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(SimilarMovieService::class.java)
        }
    }
}