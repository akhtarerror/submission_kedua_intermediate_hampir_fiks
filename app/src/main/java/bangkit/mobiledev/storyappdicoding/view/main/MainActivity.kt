package bangkit.mobiledev.storyappdicoding.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.databinding.ActivityMainBinding
import bangkit.mobiledev.storyappdicoding.di.Injection
import bangkit.mobiledev.storyappdicoding.view.adapter.LoadingStateAdapter
import bangkit.mobiledev.storyappdicoding.view.adapter.StoryPagingAdapter
import bangkit.mobiledev.storyappdicoding.view.add_story.AddStoryActivity
import bangkit.mobiledev.storyappdicoding.view.detail.DetailActivity
import bangkit.mobiledev.storyappdicoding.view.map.MapsActivity
import bangkit.mobiledev.storyappdicoding.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import bangkit.mobiledev.storyappdicoding.data.repository.Result

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyPagingAdapter: StoryPagingAdapter
    private var recyclerViewState: Parcelable? = null

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            Injection.storyRepository(this),
            Injection.userRepository(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyPagingAdapter = StoryPagingAdapter { story, optionsCompat ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.storyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyPagingAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyPagingAdapter.retry() }
            )
        }

        mainViewModel.getPagingStories().observe(this) { pagingData ->
            storyPagingAdapter.submitData(lifecycle, pagingData)
            restoreRecyclerViewState()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            doRefresh()
        }

        binding.actionLogout.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.logout().observe(this@MainActivity) { result ->
                    when (result) {
                        is Result.Success -> {
                            val welcomeIntent =
                                Intent(this@MainActivity, WelcomeActivity::class.java)
                            welcomeIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(welcomeIntent)
                            finishAffinity()
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.logout_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Result.Loading -> {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.logout_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        setupActions()
    }

    private fun doRefresh() {
        storyPagingAdapter.refresh()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.storyRecyclerView.scrollToPosition(0)
    }

    private fun setupActions() {
        binding.actionSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.actionMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = binding.storyRecyclerView.layoutManager?.onSaveInstanceState()
    }

    private fun restoreRecyclerViewState() {
        recyclerViewState?.let {
            binding.storyRecyclerView.layoutManager?.onRestoreInstanceState(it)
        }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
