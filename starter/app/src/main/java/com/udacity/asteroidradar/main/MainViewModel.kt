package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.model.Asteroid
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val _pictureOfTheDayUrl = MutableLiveData<String?>()
    val pictureOfTheDayUrl: LiveData<String?>
        get() = _pictureOfTheDayUrl

    private val _pictureOfTheDayDescription = MutableLiveData<String?>()
    val pictureOfTheDayDescription: LiveData<String?>
        get() = _pictureOfTheDayDescription

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPictureOfTheDay()
        getFeed()
    }

    private fun getFeed() {
        viewModelScope.launch {
            val feed = NasaApi.service.getAsteroidsAsync("2024-07-29", "2024-08-05").await()
            _asteroids.value = parseAsteroidsJsonResult(JSONObject(feed))
        }
    }

    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            val pictureOfTheDay = NasaApi.service.getPictureOfDayAsync().await()
            if (pictureOfTheDay.mediaType == "image") {
                _pictureOfTheDayUrl.value = pictureOfTheDay.url
                _pictureOfTheDayDescription.value = pictureOfTheDay.title
            }
        }
    }

    fun navigateToSelectedAsteroid(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun navigationToSelectedAsteroidComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}