package bangkit.mobiledev.storyappdicoding.view.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.databinding.ActivitySignupBinding
import bangkit.mobiledev.storyappdicoding.di.Injection
import bangkit.mobiledev.storyappdicoding.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        SignupViewModelFactory(Injection.userRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegisterButton()
        observeViewModel()
        setupTextWatchers()
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (isInputValid(name, email, password)) {
                signupViewModel.register(name, email, password)
            }
        }
    }

    private fun observeViewModel() {
        signupViewModel.registerResult.observe(this) { response ->
            binding.progressBar.visibility = View.GONE
            if (response != null && !response.error) {
                Toast.makeText(this, getString(R.string.regist_success), Toast.LENGTH_SHORT).show()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            } else {
                Toast.makeText(this, response?.message ?: getString(R.string.regist_fail), Toast.LENGTH_SHORT).show()
                Log.e("SignupActivity", "Error: ${response?.message}")
            }
        }

        signupViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }
    }

    private fun setupTextWatchers() {
        binding.edRegisterName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.tilName.error = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.edRegisterEmail.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.tilEmail.error = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.tilPassword.error = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.name_empty)
            isValid = false
        } else {
            binding.tilName.error = null
        }

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
