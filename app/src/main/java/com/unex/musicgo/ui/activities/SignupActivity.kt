package com.unex.musicgo.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.SignupBinding
import com.unex.musicgo.models.User
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignupActivity"

        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, SignupActivity::class.java).apply {
                Log.d(TAG, "Creating register activity intent")
            }
        }
    }

    private var _binding: SignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(this@SignupActivity)
        }

        auth = FirebaseAuth.getInstance()

        with(binding) {
            bind()
        }
    }

    private fun SignupBinding.bind() {
        Log.d(TAG, "Binding login activity")
        this.loginBtn.setOnClickListener {
            val email = this.usermail.text.toString()
            val password = this.userpassword.text.toString()
            val username = this.username.text.toString()
            val userSurname = this.usersurname.text.toString()
            signup(email, password, username, userSurname)
        }
    }

    private fun signup(email: String, password: String, username: String, userSurname: String) {
        Log.d(TAG, "Logging in")
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || userSurname.isEmpty()) {
            Toast.makeText(
                baseContext,
                "Please fill all the fields.",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        Log.d(
            TAG,
            "Email: $email, Password: $password, Username: $username, UserSurname: $userSurname"
        )
        val firestore = Firebase.firestore
        val user = hashMapOf(
            "username" to username,
            "userSurname" to userSurname,
            "email" to email,
            "password" to password,
        )
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    // Add user to database
                    firestore.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                TAG,
                                "DocumentSnapshot added with ID: ${documentReference.id}"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                    // Save the user in the local database
                    lifecycleScope.launch {
                        val userData = User(
                            email = email,
                            userSurname = userSurname,
                            username = username,
                        )
                        db?.userDao()?.deleteAll()
                        db?.userDao()?.insertUser(userData)
                    }
                    // Launch main activity
                    val intent = HomeActivity.getIntent(this)
                    startActivity(intent)
                    // Finish this activity
                    finish()
                } else {
                    // Check if the exception is because the user already exists
                    if (task.exception.toString().contains("FirebaseAuthUserCollisionException")) {
                        Toast.makeText(
                            baseContext,
                            "User already exists.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@addOnCompleteListener
                    }
                    // Check if the exception is because the email is not valid
                    if (task.exception.toString()
                            .contains("FirebaseAuthInvalidCredentialsException")
                    ) {
                        Toast.makeText(
                            baseContext,
                            "Invalid email.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@addOnCompleteListener
                    }
                    // Check if the password is too weak
                    if (task.exception.toString().contains("FirebaseAuthWeakPasswordException")) {
                        Toast.makeText(
                            baseContext,
                            "Password too weak. Must be at least 6 characters.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@addOnCompleteListener
                    }
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "Saving instance state")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Avoid memory leaks
    }
}