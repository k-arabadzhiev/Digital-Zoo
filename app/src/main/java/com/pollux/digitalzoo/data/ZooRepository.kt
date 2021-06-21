package com.pollux.digitalzoo.data


import android.util.Log
import com.pollux.digitalzoo.api.Response
import com.pollux.digitalzoo.api.ZooApi
import com.pollux.digitalzoo.util.C
import com.pollux.digitalzoo.util.Resource
import com.pollux.digitalzoo.util.networkBoundResource
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

private const val TAG = "ZooRepository"
class ZooRepository @Inject constructor(
    private val zooApi: ZooApi,
    zooDb: ZooDatabase
) {

    private val animalsDao = zooDb.animalsDao()

    suspend fun login(username: String, password: String) =
        zooApi.zookeeperLogin(username, password)

    fun getOrFetchAnimals(onFetchSuccess: () -> Unit, onFetchFailed: (Throwable) -> Unit = {  }): Flow<Resource<List<Animal>>> = networkBoundResource(
        query = {
            Log.i(TAG, "getOrFetchAnimals: query called")
            animalsDao.getAllAnimals()
        },
        fetch = {
            Log.i(TAG, "getOrFetchAnimals: fetch called")
            zooApi.getAllAnimals()
        },
        saveFetchResult = { AnimalsResponse ->
            Log.i(TAG, "getOrFetchAnimals: save called")
            animalsDao.deleteAndInsertAnimals(AnimalsResponse.animals)
        },
        //shouldFetch = ,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    fun getAnimalByName(name: String): Flow<List<Animal>> =
        animalsDao.getAnimalsByName(name)

    fun getFilteredAnimals(species: String, diet: String): Flow<List<Animal>> =
        animalsDao.getAnimalsBySpeciesAndDiet(species, diet)

    /*suspend fun addAnimal(animal: Animal, inputStream: InputStream, jwt: String?): Response {
        return if(jwt != null) {
            val photoPart = MultipartBody.Part.createFormData(
                "photo", animal.animalName, inputStream.readBytes().toRequestBody()
            )
            val token = "Bearer $jwt"
             zooApi.newAnimalPost(token, animal, photoPart)
        } else
            Response(C.UNSUCCESSFUL, "${animal.animalName} not added to database.")
    }*/

    suspend fun addOrUpdateAnimal(animal: Animal, inputStream: InputStream? = null, jwt: String?, add: Boolean): Response {
        return if(jwt != null) {
            val token = "Bearer $jwt"
            if(inputStream != null) {
                val photoPart = MultipartBody.Part.createFormData(
                    "photo", animal.animalName, inputStream.readBytes().toRequestBody()
                )
                if(add)
                    zooApi.newAnimalPost(token, animal, photoPart)
                else
                    zooApi.updateAnimalPost(token, animal, photoPart)
            }
            else
                zooApi.updateAnimalPost(token, animal)
        } else
            Response(C.UNSUCCESSFUL, "Unauthorized")
    }
}