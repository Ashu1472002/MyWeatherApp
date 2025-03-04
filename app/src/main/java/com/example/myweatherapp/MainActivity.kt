package com.example.myweatherapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx. lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import androidx. activity. viewModels
import com.example.myweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(WeatherRepository(NetworkModule.weatherService))
    }
    private lateinit var adapter: RecentSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupRecyclerView()
        setupObservers()
        setupSearchButton()

        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         //   insets
       // }
    }

    /*private fun setupViewModel() {
        val repository = WeatherRepository(NetworkModule.weatherService)
        viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(repository)
        )[WeatherViewModel::class.java]
    }*/

    private fun setupRecyclerView() {
        adapter = RecentSearchAdapter { cityName ->
            binding.editTextCity.setText(cityName)
            viewModel.getWeather(cityName)
        }

        binding.recyclerViewRecentSearches.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.weatherData.observe(this) { weather ->
            binding.apply {
                textViewCity.text = "${weather.name}, ${weather.sys.country}"
                textViewTemperature.text = "${weather.main.temp.toInt()}°C"
                textViewWeatherCondition.text = weather.weather.firstOrNull()?.description ?: ""
                textViewHumidity.text = "Humidity: ${weather.main.humidity}%"
                textViewWindSpeed.text = "Wind: ${weather.main.pressure} hPa"

                // Load weather icon
                val iconCode = weather.weather.firstOrNull()?.icon
                if (iconCode != null) {
                    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                    Glide.with(this@MainActivity)
                        .load(iconUrl)
                        .into(imageViewWeatherIcon)
                }

                weatherCardView.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.recentSearches.observe(this) { searches ->
            adapter.updateSearches(searches)
        }
    }


    private fun setupSearchButton() {
        binding.buttonSearch.setOnClickListener {
            val cityName = binding.editTextCity.text.toString().trim()
            viewModel.getWeather(cityName)
        }
    }
}