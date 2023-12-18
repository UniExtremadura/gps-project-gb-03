package com.unex.musicgo.ui.vms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.utils.UserRepository
import kotlinx.coroutines.launch

class LoginActivityViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoggedLiveData = userRepository.isLogged

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isEmpty()) {
            toastLiveData.value = "Please enter your email."
            return
        }
        if (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            toastLiveData.value = "Please enter a valid email."
            return
        }
        if (password.isEmpty()) {
            toastLiveData.value = "Please enter your password."
            return
        }
        if (password.length < 6) {
            toastLiveData.value = "Password must be at least 6 characters."
            return
        }
        userRepository.signIn(
            email = email,
            password = password,
            onSuccess = {
                viewModelScope.launch {
                    userRepository.saveUser()
                }
            },
            onFailure = {
                toastLiveData.value = it
            }
        )
    }

    companion object {
        const val TAG = "LoginActivityViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = LoginActivityViewModel(
                    app.appContainer.userRepository,
                )
                return viewModel as T
            }
        }
    }

}