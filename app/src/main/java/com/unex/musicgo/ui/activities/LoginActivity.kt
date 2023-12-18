package com.unex.musicgo.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.unex.musicgo.databinding.LoginBinding
import com.unex.musicgo.ui.vms.LoginActivityViewModel

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

    private val viewModel: LoginActivityViewModel by lazy {
        ViewModelProvider(
            this,
            LoginActivityViewModel.Factory
        )[LoginActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoginBinding.inflate(layoutInflater)
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

    private fun LoginBinding.bind() {
        Log.d(TAG, "Binding login activity")
        this.loginBtn.setOnClickListener {
            val emailText = this.username.text.toString()
            val passwordText = this.passwordTv.text.toString()
            viewModel.signInWithEmailAndPassword(emailText, passwordText)
        }
        this.registerBtn.setOnClickListener {
            val intent = SignupActivity.newIntent(this@LoginActivity)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Avoid memory leaks
    }
}