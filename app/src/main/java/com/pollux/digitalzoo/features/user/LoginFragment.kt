package com.pollux.digitalzoo.features.user

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.databinding.LoginFragmentBinding
import com.pollux.digitalzoo.util.exhaustive
import com.pollux.digitalzoo.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val zookeeperViewModel: ZookeeperViewModel by activityViewModels()
    //private lateinit var savedStateHandle: SavedStateHandle

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = LoginFragmentBinding.bind(view)
        //savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        //savedStateHandle.set("loginState", false)
        val navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.animalsFragment, false)
            .build()

        if (zookeeperViewModel.loginState) {
            navController.navigate(R.id.addEditAnimalFragment, null, navOptions)
        }

        zookeeperViewModel.snackbar.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSnackbar(it, Snackbar.LENGTH_SHORT)
                zookeeperViewModel.onSnackbarShown()
            }
        }

        zookeeperViewModel.loginUiState.observe(viewLifecycleOwner) { loginState ->
            binding.progressBar.isVisible = loginState == ZookeeperViewModel.LoginUiState.Loading
            when (loginState) {
                is ZookeeperViewModel.LoginUiState.Success -> {
                    navController.navigate(R.id.addEditAnimalFragment, null, navOptions)
                }
                is ZookeeperViewModel.LoginUiState.Error -> {
                    Log.i(TAG, "onViewCreated: ${loginState.message}")
                }
                is ZookeeperViewModel.LoginUiState.Loading -> {
                    Log.i(TAG, "onViewCreated: $loginState")
                }
                is ZookeeperViewModel.LoginUiState.Empty -> {
                    Log.i(TAG, "onViewCreated: $loginState")
                }
            }.exhaustive
        }

        binding.apply {
            loginButton.setOnClickListener {
                login(
                    usernameEditText.text.toString().trim(),
                    passwordEditText.text.toString().trim()
                )
            }
            cancelButton.setOnClickListener {
                findNavController().navigate(R.id.animalsFragment)
            }
        }
    }

    private fun login(username: String, password: String) {
        zookeeperViewModel.login(username, password)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}