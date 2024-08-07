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
import com.udacity.asteroidradar.getSevenDaysFromNowDate
import com.udacity.asteroidradar.getTodaysDate
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val context: Context, private val database: AsteroidsDatabase) {

    val allAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroids().map { it.asDomainModel() }
    val todaysAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsByDate(getTodaysDate()).map { it.asDomainModel() }
    val weekAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsByInterval(getTodaysDate(), getSevenDaysFromNowDate())
            .map { it.asDomainModel() }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {

            val connectivityManager = context.getSystemService<ConnectivityManager>()
            val activeNetwork = connectivityManager?.activeNetworkInfo
            val isConnected = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                val asteroidsResponse =
                    NasaApi.service.getAsteroidsAsync(startDate, endDate).await()
                val asteroidsList = parseAsteroidsJsonResult(JSONObject(asteroidsResponse))
                database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
            } else {
                Log.d("AsteroidsRepository", "No internet connection.")
            }
        }
    }
}