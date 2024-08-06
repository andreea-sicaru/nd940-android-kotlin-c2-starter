package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val _pictureOfTheDayUrl = MutableLiveData<String?>()
    val pictureOfTheDayUrl: LiveData<String?>
        get() = _pictureOfTheDayUrl

    private val _pictureOfTheDayDescription = MutableLiveData<String?>()
    val pictureOfTheDayDescription: LiveData<String?>
        get() = _pictureOfTheDayDescription


    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedAsteroid: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteroids = asteroidsRepository.asteroids

    init {
        getPictureOfTheDay()
        refreshAsteroids()
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
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