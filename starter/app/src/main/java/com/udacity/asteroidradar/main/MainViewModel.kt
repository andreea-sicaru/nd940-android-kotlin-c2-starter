package com.udacity.asteroidradar.main

import android.app.Application
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application) : ViewModel() {

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedAsteroid: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(application.applicationContext, database)

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
            val connectivityManager =
                application.applicationContext.getSystemService<ConnectivityManager>()
            val activeNetwork = connectivityManager?.activeNetworkInfo
            val isConnected = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                val pictureOfTheDay = NasaApi.service.getPictureOfDayAsync().await()
                if (pictureOfTheDay.mediaType == "image") {
                    _pictureOfTheDay.value = pictureOfTheDay
                }
            } else {
                Log.d("AsteroidsRepository", "No internet connection.")
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