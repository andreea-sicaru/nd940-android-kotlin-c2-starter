package com.udacity.asteroidradar.model

import android.os.Parcelable
import com.udacity.asteroidradar.database.DatabaseAsteroid
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

fun ArrayList<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    val dbModels = ArrayList<DatabaseAsteroid>()

    for (asteroid in this) {
        dbModels.add(
            DatabaseAsteroid(
                id = asteroid.id,
                codename = asteroid.codename,
                closeApproachDate = asteroid.closeApproachDate,
                absoluteMagnitude = asteroid.absoluteMagnitude,
                estimatedDiameter = asteroid.estimatedDiameter,
                relativeVelocity = asteroid.relativeVelocity,
                distanceFromEarth = asteroid.distanceFromEarth,
                isPotentiallyHazardous = asteroid.isPotentiallyHazardous
            )
        )
    }

    return dbModels.toTypedArray()
}