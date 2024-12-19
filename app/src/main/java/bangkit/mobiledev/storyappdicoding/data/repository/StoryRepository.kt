package bangkit.mobiledev.storyappdicoding.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.mobiledev.storyappdicoding.data.StoryRemoteMediator
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.response.AddNewStoryResponse
import bangkit.mobiledev.storyappdicoding.data.response.DetailStoryResponse
import bangkit.mobiledev.storyappdicoding.data.response.StoryResponse
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiService
import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity
import bangkit.mobiledev.storyappdicoding.database.room.StoryDatabase
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

open class StoryRepository(private val apiService: ApiService, private val userPreferences: UserPreferences, private val database: StoryDatabase) {

    suspend fun getDetailStory(token: String, id: String): DetailStoryResponse {
        return apiService.getDetailStory(token, id)
    }

    suspend fun addNewStory(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): AddNewStoryResponse {
        val token = userPreferences.getToken().first()
        val authorizationToken = "Bearer $token"

        database.storyDao().clearAll()
        database.remoteKeysDao().deleteRemoteKeys()

        return apiService.addNewStory(authorizationToken, description, photo, lat, lon)
    }

    suspend fun getAllStoriesWithLocation(location: Int = 1): StoryResponse {
        val token = userPreferences.getToken().first()
        val authorizationToken = "Bearer $token"

        return apiService.getStoriesWithLocation(authorizationToken ,location)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStory(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10 ),
            remoteMediator = StoryRemoteMediator(database, apiService, userPreferences, sortOrder = "createdAt DESC"),
            pagingSourceFactory = { database.storyDao().getAllStories() }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences, database)
            }.also { instance = it }
    }
}