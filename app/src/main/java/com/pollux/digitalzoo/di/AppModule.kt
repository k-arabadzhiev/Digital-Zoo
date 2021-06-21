package com.pollux.digitalzoo.di

import android.app.Application
import androidx.room.Room
import com.pollux.digitalzoo.api.ZooApi
import com.pollux.digitalzoo.data.ZooDatabase
import com.pollux.digitalzoo.util.C
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(C.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideZooApi(retrofit: Retrofit): ZooApi =
        retrofit.create(ZooApi::class.java)


    @Provides
    @Singleton
    fun provideDatabase(app: Application): ZooDatabase =
        Room.databaseBuilder(app, ZooDatabase::class.java, "zoo_database")
            .fallbackToDestructiveMigration()
            .build()
}
