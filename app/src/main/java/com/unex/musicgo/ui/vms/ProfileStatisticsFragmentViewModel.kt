package com.unex.musicgo.ui.vms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.User
import com.unex.musicgo.utils.UserRepository

class ProfileStatisticsFragmentViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    val user: LiveData<User> = userRepository.currentUser

    companion object {
        const val TAG = "ProfileStatisticsFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = ProfileStatisticsFragmentViewModel(
                    app.appContainer.userRepository
                )
                return viewModel as T
            }
        }
    }
}