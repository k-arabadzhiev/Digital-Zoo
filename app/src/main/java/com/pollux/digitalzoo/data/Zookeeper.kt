package com.pollux.digitalzoo.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Zookeeper(
    val username: String,
    val password: String,
    val jwt: String,
    val tokenValidDate: Long
)
