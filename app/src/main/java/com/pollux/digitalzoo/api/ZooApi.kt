package com.pollux.digitalzoo.api

import com.pollux.digitalzoo.data.Animal
import okhttp3.MultipartBody
import retrofit2.http.*

interface ZooApi {

    @GET("animals/all")
    suspend fun getAllAnimals(): AnimalsResponse

    @FormUrlEncoded
    @POST("zookeeper/login")
    suspend fun zookeeperLogin(
        @Field("username") username: String,
        @Field("password") password: String
    ): ZookeeperResponse

    @Multipart
    @POST("animal/new")
    suspend fun newAnimalPost(
        @Header("Authorization") token: String,
        @Part("animal") animal: Animal,
        @Part photo: MultipartBody.Part
    ) : Response

    @Multipart
    @POST("animal/update")
    suspend fun updateAnimalPost(
        @Header("Authorization") token: String,
        @Part("animal") animal: Animal,
        @Part photo: MultipartBody.Part? = null
    ) : Response


}