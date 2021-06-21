package com.pollux.digitalzoo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "animal")
data class Animal(
    @PrimaryKey val id: Int = 0,
    val animalName: String,
    val animalNameOld: String? = null,
    val animalPhoto: String,
    val zookeeper: String? = null,
    val age: String,
    val diet: String,
    val food: String,
    val info: String,
    val species: String,
    val weight: String,
    val habitat: String
) : Parcelable
