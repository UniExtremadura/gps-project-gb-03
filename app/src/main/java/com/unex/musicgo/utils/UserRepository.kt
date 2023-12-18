package com.unex.musicgo.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.database.dao.UserDao
import com.unex.musicgo.models.User
import com.unex.musicgo.ui.fragments.SettingsFragment
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) {

    private val TAG = "UserRepository"

    private val _isLogged = MutableLiveData(false)
    val isLogged: LiveData<Boolean> = _isLogged

    private val _currentEmail = MutableLiveData<String?>()
    val currentEmail: LiveData<String?> = _currentEmail

    val currentUser: LiveData<User> = currentEmail.switchMap { email ->
        if (email != null) {
            userDao.getUserByEmail(email)
        } else {
            MutableLiveData()
        }
    }

    init {
        if(firebaseAuth.currentUser != null) {
            _currentEmail.value = firebaseAuth.currentUser!!.email
            _isLogged.value = true
        }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit = {},
        onFailure: (message: String) -> Unit = {}
    ) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure("Authentication failed.")
                    }
                }
    }

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
        userSurname: String,
        onSuccess: () -> Unit = {},
        onFailure: (message: String) -> Unit = {}
    ) {
        try {
            val user = hashMapOf(
                "username" to username,
                "userSurname" to userSurname,
                "email" to email,
                "password" to password,
            )
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Firebase.firestore.collection("users").add(user).await()
            saveUser()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Error signing up")
        }
    }

    suspend fun saveUser() {
        val email = firebaseAuth.currentUser?.email
        val userCollection = Firebase.firestore.collection("users")
        val document = userCollection.whereEqualTo("email", email).get().await()
        val user = User(
            email = email!!,
            userSurname = document.documents[0].data!!["userSurname"].toString(),
            username = document.documents[0].data!!["username"].toString(),
        )
        userDao.deleteAll()
        userDao.insertUser(user)
        _isLogged.postValue(true)
        _currentEmail.postValue(email)
    }

    /**
     * Sign out the current user.
     */
    suspend fun signOut(
        onSuccess: () -> Unit = {},
        onFailure: (message: String) -> Unit = {}
    ) {
        try {
            firebaseAuth.signOut()
            userDao.deleteAll()
            _currentEmail.postValue(null)
            _isLogged.postValue(false)
            onSuccess()
        } catch (e: Exception) {
            onFailure("Error signing out: $e")
        }
    }

    suspend fun deleteAccount(
        success: () -> Unit = {},
        error: (message: String) -> Unit = {}
    ) {
        val email = currentUser.value?.email
        Log.d(TAG, "Email: $email")

        // Get the user collection
        val userCollection = Firebase.firestore.collection("users").get().await()
        if (userCollection == null) {
            Log.d(SettingsFragment.TAG, "Error getting documents: users")
            error("Error getting documents: users")
            return
        }

        // Get the user document
        val userDoc = userCollection.documents.filter {
            it.data?.get("email") == email
        }
        if (userDoc.isEmpty()) {
            Log.d(SettingsFragment.TAG, "Error getting documents: user")
            error("Error getting documents: user")
            return
        }

        try {
            Firebase.firestore.collection("users").document(userDoc[0].id).delete().await()
            firebaseAuth.currentUser!!.delete().await()
            success()
        } catch (e: Exception) {
            // Restore the user document and the user from the authentication
            try {
                Firebase.firestore.collection("users").add(userDoc[0].data!!).await()
            } catch (e: Exception) {
                error("Error restoring user: $e")
            }
            error("Error deleting user: $e")
        }
    }

}

