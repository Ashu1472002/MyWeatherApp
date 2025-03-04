package com.example.myweatherapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

annotation class BuildConfig

interface WeatherService {


    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<WeatherResponse> // Changed from okhttp3.Response to retrofit2.Response
}


