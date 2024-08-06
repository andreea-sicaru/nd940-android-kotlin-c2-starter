package com.udacity.asteroidradar.repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.getSystemService
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

class AsteroidsRepository(private val context: Context, private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroids().map { it.asDomainModel() }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {

            val connectivityManager = context.getSystemService<ConnectivityManager>()
            val activeNetwork = connectivityManager?.activeNetworkInfo
            val isConnected = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                val asteroidsResponse =
                    NasaApi.service.getAsteroidsAsync("2024-07-30", "2024-08-06").await()
                val asteroidsList = parseAsteroidsJsonResult(JSONObject(asteroidsResponse))
                database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
            } else {
                Log.d("AsteroidsRepository", "No internet connection.")
            }
        }
    }
}