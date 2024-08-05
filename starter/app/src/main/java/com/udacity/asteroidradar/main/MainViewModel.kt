package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val _pictureOfTheDayUrl = MutableLiveData<String?>()
    val pictureOfTheDayUrl: LiveData<String?>
        get() = _pictureOfTheDayUrl

    private val _pictureOfTheDayDescription = MutableLiveData<String?>()
    val pictureOfTheDayDescription: LiveData<String?>
        get() = _pictureOfTheDayDescription

    init {
        getPictureOfTheDay()
        getFeed()
    }

    private fun getFeed() {
        viewModelScope.launch {
            val feed = NasaApi.service.getAsteroidsAsync("2024-08-01", "2024-08-02").await()
            val asteroidsList = parseAsteroidsJsonResult(JSONObject(feed))
            System.out.println(feed)
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
}