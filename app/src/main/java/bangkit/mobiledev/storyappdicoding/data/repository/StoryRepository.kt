package bangkit.mobiledev.storyappdicoding.data.repository

import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.response.AddNewStoryResponse
import bangkit.mobiledev.storyappdicoding.data.response.DetailStoryResponse
import bangkit.mobiledev.storyappdicoding.data.response.StoryResponse
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun getAllStories(page: Int? = 1, size: Int? = 7, location: Int = 0): StoryResponse {
        val token = userPreferences.getToken().first()
        val authorizationToken = "Bearer $token"
        return apiService.getAllStories(authorizationToken, page, size, location)
    }

    suspend fun getDetailStory(token: String, id: String): DetailStoryResponse {
        return apiService.getDetailStory(token, id)
    }

    suspend fun addNewStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): AddNewStoryResponse {
        val token = userPreferences.getToken().first()
        val authorizationToken = "Bearer $token"
        return apiService.addNewStory(authorizationToken, description, photo, lat, lon)
    }

    suspend fun getAllStoriesWithMap(location: Int = 1): StoryResponse {
        val token = userPreferences.getToken().first()
        val authorizationToken = "Bearer $token"

        return apiService.getStoriesWithLocation(authorizationToken ,location)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences)
            }.also { instance = it }
    }
}
