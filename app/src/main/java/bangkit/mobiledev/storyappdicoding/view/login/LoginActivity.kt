package bangkit.mobiledev.storyappdicoding.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import  bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiConfig
import bangkit.mobiledev.storyappdicoding.databinding.ActivityLoginBinding
import bangkit.mobiledev.storyappdicoding.view.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userPreferences by lazy { UserPreferences(applicationContext) }
    private val userRepository by lazy {
        UserRepository(
            ApiConfig.getApiService()
        )
    }
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(userRepository, userPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.edLoginEmail.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.tilEmail.error = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.isNotEmpty()) {
                    binding.tilPassword.error = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (isValidInput(email, password)) {
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { loginResponse ->
            binding.progressBar.visibility = View.GONE
            if (loginResponse != null && !loginResponse.error) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.errorMessage.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            if (errorMessage != null) {
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.email_empty)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.pass_emp)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

}
