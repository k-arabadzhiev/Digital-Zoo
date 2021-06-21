package com.pollux.digitalzoo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<Animal>)

    @Query(
        """
        SELECT * 
        FROM animal 
        WHERE animalName LIKE '%' || :name || '%' 
        ORDER BY animalName"""
    )
    fun getAnimalsByName(name: String): Flow<List<Animal>>

    @Query(
        """
        SELECT * 
        FROM animal 
        WHERE UPPER(diet) = (
            CASE 
                WHEN UPPER(:diet) = 'ALL' 
                THEN UPPER(diet) 
                ELSE UPPER(:diet) 
            END
        ) 
        AND UPPER(species) = (
            CASE 
                WHEN UPPER(:species) = 'ALL' 
                THEN UPPER(species) 
                ELSE UPPER(:species) 
            END
        )
        """
    )
    fun getAnimalsBySpeciesAndDiet(species: String, diet: String): Flow<List<Animal>>

    @Query("SELECT * FROM animal")
    fun getAllAnimals(): Flow<List<Animal>>

    @Query("DELETE FROM animal")
    suspend fun deleteAllAnimals()

    @Transaction
    suspend fun deleteAndInsertAnimals(animals: List<Animal>){
        deleteAllAnimals()
        insertAnimals(animals)
    }
}
