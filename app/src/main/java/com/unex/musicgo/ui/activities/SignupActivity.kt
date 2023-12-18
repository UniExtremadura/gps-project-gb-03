package com.unex.musicgo.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.unex.musicgo.databinding.SignupBinding
import com.unex.musicgo.ui.vms.SignUpActivityViewModel

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

    private val viewModel: SignUpActivityViewModel by lazy {
        ViewModelProvider(
            this,
            SignUpActivityViewModel.Factory
        )[SignUpActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        binding.bind()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.isLoggedLiveData.observe(this) {
            if (it) {
                val intent = HomeActivity.getIntent(this)
                startActivity(intent)
                finish()
            }
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
        if (!viewModel.validateCredentials(email, password, username, userSurname)) {
            return
        }
        Log.d(
            TAG,
            "Email: $email, Password: $password, Username: $username, UserSurname: $userSurname"
        )
        viewModel.signUp(email, password, username, userSurname)
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