package com.pollux.digitalzoo.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.pollux.digitalzoo.data.Zookeeper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
                Log.e(TAG, "Error reading preferences ", exception)
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val username = preferences[PreferencesKeys.USERNAME] ?: ""
            val password = preferences[PreferencesKeys.PASSWORD] ?: ""
            //val firstName = preferences[PreferencesKeys.FIRST_NAME] ?: ""
            //val lastName = preferences[PreferencesKeys.LAST_NAME] ?: ""
            val jwt = preferences[PreferencesKeys.JWT] ?: ""
            val date = preferences[PreferencesKeys.EXPIRY_DATE] ?: 0
            Zookeeper(username, password,/* firstName, lastName,*/ jwt, date)
        }

    suspend fun updateZookeeper(
        username: String,
        password: String,
       /* firstName: String,
        lastName: String,*/
        jwt: String,
        date: Long
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
            preferences[PreferencesKeys.PASSWORD] = password
            /*preferences[PreferencesKeys.FIRST_NAME] = firstName
            preferences[PreferencesKeys.LAST_NAME] = lastName*/
            preferences[PreferencesKeys.JWT] = jwt
            preferences[PreferencesKeys.EXPIRY_DATE] = date
            Log.i(TAG, "updateZookeeper: $date")
        }
    }

    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        //val FIRST_NAME = stringPreferencesKey("first_name")
        //val LAST_NAME = stringPreferencesKey("last_name")
        val JWT = stringPreferencesKey("jwt")
        val EXPIRY_DATE = longPreferencesKey("date")
    }

}