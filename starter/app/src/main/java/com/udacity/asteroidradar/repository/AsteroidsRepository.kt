package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroids().map { it.asDomainModel() }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids = NasaApi.service.getAsteroidsAsync("2024-07-30", "2024-08-06").await()
            val asteroidsList = parseAsteroidsJsonResult(JSONObject(asteroids))
            Log.d("Andreea", "refreshAsteroids $asteroidsList")
            database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
        }
    }
}