package bangkit.mobiledev.storyappdicoding.di

import android.content.Context
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiConfig
import bangkit.mobiledev.storyappdicoding.view.add_story.AddStoryViewModelFactory
import bangkit.mobiledev.storyappdicoding.view.map.MapsViewModelFactory

object Injection {
    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        UserPreferences(context)
        return UserRepository.getInstance(apiService)
    }

    fun storyRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        return StoryRepository.getInstance(apiService, userPreferences)
    }

    fun provideAddStoryViewModelFactory(context: Context): AddStoryViewModelFactory {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        val storyRepository = StoryRepository(apiService, userPreferences)
        return AddStoryViewModelFactory(storyRepository)
    }

    fun provideStoryMapViewModelFactory(context: Context): MapsViewModelFactory {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        val storyRepository = StoryRepository(apiService, userPreferences)
        return MapsViewModelFactory(storyRepository)
    }
}
