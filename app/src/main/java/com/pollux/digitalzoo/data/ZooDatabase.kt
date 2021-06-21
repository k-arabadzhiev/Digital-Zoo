package com.pollux.digitalzoo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Animal::class],
    version = 1, exportSchema = false
)
abstract class ZooDatabase : RoomDatabase() {

    abstract fun animalsDao(): AnimalsDao
}