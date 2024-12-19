package bangkit.mobiledev.storyappdicoding.di

import android.content.Context
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiConfig
import bangkit.mobiledev.storyappdicoding.database.room.StoryDatabase
import bangkit.mobiledev.storyappdicoding.view.add_story.AddStoryViewModelFactory
import bangkit.mobiledev.storyappdicoding.view.map.MapViewModelFactory

object Injection {
    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        return UserRepository.getInstance(apiService, userPreferences)
    }

    fun storyRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, userPreferences, database)
    }

    fun provideAddStoryViewModelFactory(context: Context): AddStoryViewModelFactory {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        val database = StoryDatabase.getInstance(context)
        val storyRepository = StoryRepository(apiService, userPreferences, database)
        return AddStoryViewModelFactory(storyRepository)
    }

    fun provideStoryMapViewModelFactory(context: Context): MapViewModelFactory {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context)
        val database = StoryDatabase.getInstance(context)
        val storyRepository = StoryRepository(apiService, userPreferences, database)
        return MapViewModelFactory(storyRepository)
    }
}
