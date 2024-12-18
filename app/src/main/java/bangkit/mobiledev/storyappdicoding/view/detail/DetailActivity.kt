package bangkit.mobiledev.storyappdicoding.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.di.Injection
import com.bumptech.glide.Glide
import bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.databinding.ActivityDetailBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(Injection.storyRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val storyId = intent.getStringExtra("STORY_ID") ?: ""

        val token = getTokenFromPreferences()

        if (storyId.isNotEmpty() && token != null) {
            observeViewModel()
            viewModel.getDetailStory("Bearer $token", storyId)
        } else {
            Toast.makeText(this, getString(R.string.story_detail_fail), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getTokenFromPreferences(): String? {
        val userPreferences = UserPreferences(this)
        return runBlocking { userPreferences.getToken().first() }
    }

    private fun observeViewModel() {
        viewModel.storyDetail.observe(this) { story ->
            binding.progressBar.visibility = View.GONE
            if (story != null) {
                binding.tvDetailName.text = story.name
                binding.tvDetailDescription.text = story.description
                Glide.with(this)
                    .load(story.photoUrl)
                    .into(binding.ivDetailPhoto)
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
