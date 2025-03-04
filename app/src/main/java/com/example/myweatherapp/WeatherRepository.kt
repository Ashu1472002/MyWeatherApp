package com.example.myweatherapp

class WeatherRepository(private val weatherService: WeatherService) {
    suspend fun getWeatherForCity(cityName: String): Result<WeatherResponse> {
        return try {
            val response = weatherService.getCurrentWeather(cityName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}