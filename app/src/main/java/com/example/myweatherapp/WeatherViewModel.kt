package com.example.myweatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _recentSearches = MutableLiveData<List<String>>(emptyList())
    val recentSearches: LiveData<List<String>> = _recentSearches

    fun getWeather(cityName: String) {
        if (cityName.isBlank()) {
            _error.value = "Please enter a city name"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            repository.getWeatherForCity(cityName)
                .onSuccess {
                    _weatherData.value = it
                    addToRecentSearches(cityName)
                }
                .onFailure { _error.value = it.message ?: "Unknown error" }
            _isLoading.value = false
        }
    }

    private fun addToRecentSearches(cityName: String) {
        val currentList = _recentSearches.value ?: emptyList()
        val newList = (listOf(cityName) + currentList)
            .distinct()
            .take(5)
        _recentSearches.value = newList
    }
}
