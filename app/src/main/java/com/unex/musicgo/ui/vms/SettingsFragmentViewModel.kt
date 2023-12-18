package com.unex.musicgo.ui.vms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.User
import com.unex.musicgo.utils.UserRepository
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragmentViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    val user: LiveData<User> = userRepository.currentUser

    fun deleteAccount(success: () -> Unit, error: (message: String) -> Unit) {
        viewModelScope.launch {
            userRepository.deleteAccount(
                success = {
                    success()
                },
                error = {
                    error(it)
                }
            )
        }
    }

    fun logOut(success: () -> Unit) {
        viewModelScope.launch {
            userRepository.signOut(
                onSuccess = {
                    success()
                }
            )
        }
    }

    fun getLang(currentLocale: Locale): String {
        val languageInfoName = when (currentLocale.language) {
            "en" -> "English"
            "es" -> "Español"
            else -> "English"
        }
        return languageInfoName
    }

    companion object {
        const val TAG = "SettingsFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = SettingsFragmentViewModel(
                    app.appContainer.userRepository
                )
                return viewModel as T
            }
        }
    }
}