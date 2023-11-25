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
import com.unex.musicgo.databinding.LoginBinding
import com.unex.musicgo.models.User
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"

        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, LoginActivity::class.java).apply {
                Log.d(TAG, "Creating login activity intent")
            }
        }
    }

    private var _binding: LoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(this@LoginActivity)
        }

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // auth.signOut()

            // Launch the main activity
            val intent = HomeActivity.getIntent(this)
            startActivity(intent)
            // Finish login activity and go to main activity
            finish()
        }

        with(binding) {
            bind()
        }
    }

    private fun LoginBinding.bind() {
        Log.d(TAG, "Binding login activity")
        this.loginBtn.setOnClickListener {
            val emailText = this.username.text.toString()
            val passwordText = this.passwordTv.text.toString()
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            login(emailText, passwordText)
        }
        this.registerBtn.setOnClickListener {
            val intent = SignupActivity.newIntent(this@LoginActivity)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        Log.d(TAG, "Logging in")
        Log.d(TAG, "Username: $email, Password: $password")
        // Try to get the user from the firebase database
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    // Save the user
                    saveUser()
                    // Launch the main activity
                    val intent = HomeActivity.getIntent(this)
                    startActivity(intent)
                    // Finish login activity and go to main activity
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun saveUser() {
        val email = auth.currentUser?.email
        Log.d(TAG, "Saving user $email")
        val firestore = Firebase.firestore
        // Get the user data from the database
        val userCollection = firestore.collection("users")
        // Search the document whose field email is equal to the user email. Only one document is expected.
        userCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userEmail = document.data["email"].toString()
                    // val password = document.data["password"].toString()
                    val userSurname = document.data["userSurname"].toString()
                    val username = document.data["username"].toString()
                    // Save the user in the database
                    lifecycleScope.launch {
                        val user = User(
                            email = userEmail,
                            userSurname = userSurname,
                            username = username,
                        )
                        db?.userDao()?.deleteAll()
                        db?.userDao()?.insertUser(user)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
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