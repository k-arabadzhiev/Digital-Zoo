package com.pollux.digitalzoo.api

import com.pollux.digitalzoo.data.Animal
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnimalsResponse(val animals: List<Animal>)

@JsonClass(generateAdapter = true)
data class ZookeeperResponse(
    val status: String? = null,
    val message: String? = null,
    val jwt: String? = null
)

@JsonClass(generateAdapter = true)
data class Response(
    val status: String,
    val body: String
)