package com.unex.musicgo.ui.vms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.utils.UserRepository
import kotlinx.coroutines.launch

class SignUpActivityViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoggedLiveData = userRepository.isLogged

    fun validateCredentials(email: String, password: String, username: String, userSurname: String): Boolean {
        if (email.isEmpty()) {
            toastLiveData.value = "Please enter your email."
            return false
        }
        if (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            toastLiveData.value = "Please enter a valid email."
            return false
        }
        if (password.isEmpty()) {
            toastLiveData.value = "Please enter your password."
            return false
        }
        if (password.length < 6) {
            toastLiveData.value = "Password must be at least 6 characters."
            return false
        }
        if (username.isEmpty()) {
            toastLiveData.value = "Please enter your username."
            return false
        }
        if (userSurname.isEmpty()) {
            toastLiveData.value = "Please enter your surname."
            return false
        }
        return true
    }

    fun signUp(email: String, password: String, username: String, userSurname: String) {
        viewModelScope.launch {
            userRepository.signUp(
                email = email,
                password = password,
                username = username,
                userSurname = userSurname,
                onFailure = {
                    toastLiveData.value = it
                }
            )
        }
    }

    companion object {
        const val TAG = "SignUpActivityViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = SignUpActivityViewModel(
                    app.appContainer.userRepository
                )
                return viewModel as T
            }
        }
    }

}