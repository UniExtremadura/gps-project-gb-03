package com.unex.musicgo.ui.vms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesFragmentViewModel: ViewModel() {

    val toastLiveData = MutableLiveData<String>()

    private val _stars = MutableLiveData(0)
    val stars: LiveData<Int> get() = _stars

    fun updateStars(stars: Int) {
        if (stars in 4..5) {
            // Update the value of new stars. When the user click the same stars, the value is 0.
            _stars.value = if (stars == _stars.value) 0 else stars
        } else {
            throw IllegalArgumentException("Stars must be 0 or between 4 and 5")
        }
    }
}