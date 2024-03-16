package com.example.weather.model.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FAVOURITE_CITIES")
data class LocationData(
    @PrimaryKey
    val cityName: String,
    val latitude: Double,
    val longitude: Double
): Serializable