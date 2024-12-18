package bangkit.mobiledev.storyappdicoding.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.view.main.MainActivity
import bangkit.mobiledev.storyappdicoding.view.welcome.WelcomeActivity
import  bangkit.mobiledev.storyappdicoding.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = UserPreferences(this)

        lifecycleScope.launch {
            kotlinx.coroutines.delay(2000)

            val token = userPreferences.getToken().first()

            if (token.isNullOrEmpty()) {
                val welcomeIntent = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                startActivity(welcomeIntent)
            } else {
                val mainIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
                finish()
            }
            finish()
        }
    }
}
