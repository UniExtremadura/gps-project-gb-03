package com.unex.musicgo.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unex.musicgo.MainActivity
import com.unex.musicgo.databinding.LoginBinding


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            /* auth.signOut()*/

             // Launch the main activity
             val intent = MainActivity.getIntent(this)
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
                    // Launch the main activity
                    val intent = MainActivity.getIntent(this)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "Saving instance state")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Avoid memory leaks
    }
}