package com.pollux.digitalzoo.features.user

import android.util.Log
import androidx.lifecycle.*
import com.pollux.digitalzoo.api.ZookeeperResponse
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.data.ZooRepository
import com.pollux.digitalzoo.data.Zookeeper
import com.pollux.digitalzoo.util.C
import com.pollux.digitalzoo.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.InputStream
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ZookeeperViewModel"

@HiltViewModel
class ZookeeperViewModel @Inject constructor(
    private val repository: ZooRepository,
    private val preferencesManager: PreferencesManager,
    val state: SavedStateHandle
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asLiveData()

    var loginState = state.get<Boolean>("loginState") ?: false
        set(value) {
            field = value
            state.set("loginState", value)
            Log.i(TAG, "loginState set to $value")
        }

    private var jwt: String? = null
    var zookeeper: String? = null

    init {
        Log.i(TAG, "loginState: $loginState")
        viewModelScope.launch {
            val currentZookeeper = preferencesManager.preferencesFlow.first()
            Log.i(TAG, "currentZookeeper: $currentZookeeper")
            if (currentZookeeper.username.isNotBlank() && currentZookeeper.password.isNotBlank()) {
                Log.i(TAG, "calling login...")
                login(currentZookeeper.username, currentZookeeper.password)
                jwt = currentZookeeper.jwt
                zookeeper = currentZookeeper.username
                loginState = true
                Log.i(TAG, "jwt set.")
            }
        }
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginUiState.emit(LoginUiState.Loading)
        Log.i(TAG, "login: starting..")
        try {
            val response = repository.login(username, password)
            if (response.status == C.OK) {
                Log.i(TAG, "login: success")
                _loginUiState.emit(LoginUiState.Success)
                loginState = true
                jwt = response.jwt!!
                zookeeper = username
                val zookeeper = responseToZookeeper(username, password, response)
                preferencesManager.updateZookeeper(
                    username, password,
                    zookeeper.jwt,
                    zookeeper.tokenValidDate
                )
                _snackbar.value = "Login successful!"
            } else {
                _loginUiState.emit(LoginUiState.Error(response.message!!))
                _snackbar.value = "Incorrect username or password!"
                loginState = false
            }
        } catch (e: HttpException) {
            _loginUiState.emit(LoginUiState.Error(e.message!!))
            loginState = false
        } catch (e: SocketTimeoutException) {
            _loginUiState.emit(LoginUiState.Error("Failed to connect to the server!"))
            _snackbar.value = "Network connection failed!"
            loginState = false
        } catch (e: ConnectException) {
            _loginUiState.emit(LoginUiState.Error("Failed to connect to the server!"))
            _snackbar.value = "Network connection failed!"
            loginState = false
        }
    }

    private fun responseToZookeeper(
        username: String,
        password: String,
        response: ZookeeperResponse
    ) = Zookeeper(
        username, password,
        response.jwt!!,
        tokenValidDate = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)
    )

    /*fun addNewAnimal(newAnimal: Animal, inputStream: InputStream) {
        viewModelScope.launch {
            try {
                val response = repository.addAnimal(newAnimal, inputStream, jwt)
                _snackbar.value = response.body
            } catch (e: HttpException) {
                _snackbar.value = e.message
            } catch (e: SocketTimeoutException) {
                _snackbar.value = e.message
            }
        }
    }*/

    fun addOrUpdateAnimal(updatedAnimal: Animal, inputStream: InputStream?, add: Boolean) {
        viewModelScope.launch {
            try {
                val response = repository.addOrUpdateAnimal(updatedAnimal, inputStream, jwt, add)
                _snackbar.value = response.body
            } catch (e: HttpException) {
                _snackbar.value = e.message
            } catch (e: SocketTimeoutException) {
                _snackbar.value = e.message
            }
        }
    }

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?> get() = _snackbar

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    sealed class LoginUiState {
        object Success : LoginUiState()
        data class Error(val message: String) : LoginUiState()
        object Loading : LoginUiState()
        object Empty : LoginUiState()
    }
}