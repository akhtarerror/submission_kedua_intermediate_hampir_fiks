package bangkit.mobiledev.storyappdicoding.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiConfig
import bangkit.mobiledev.storyappdicoding.databinding.ActivityMainBinding
import bangkit.mobiledev.storyappdicoding.view.adapter.StoryPagingAdapter
import bangkit.mobiledev.storyappdicoding.view.add_story.AddStoryActivity
import bangkit.mobiledev.storyappdicoding.view.map.MapsActivity
import bangkit.mobiledev.storyappdicoding.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoryPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = UserPreferences(this)
        val repository = StoryRepository.getInstance(ApiConfig.getApiService(), userPreferences)
        mainViewModel = MainViewModel(repository)

        // Setup RecyclerView with StoryPagingAdapter
        adapter = StoryPagingAdapter()
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.storyRecyclerView.adapter = adapter

        // Observe PagingData and submit it to the adapter
        lifecycleScope.launch {
            mainViewModel.stories.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // Setup Pull-to-Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Add story button
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Navigate to MapsActivity
        binding.actionMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        // Navigate to locale settings
        binding.actionSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        // Logout action
        binding.actionLogout.setOnClickListener {
            // Clear token from UserPreferences (to log out)
            lifecycleScope.launch {
                userPreferences.clearToken() // Remove user's token
            }

            // Redirect to WelcomeActivity after logout
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear previous activities
            startActivity(intent)
            finish() // Close MainActivity after logout
        }
    }
}
