package bangkit.mobiledev.storyappdicoding.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiService
import bangkit.mobiledev.storyappdicoding.database.entity.RemoteKeysEntity
import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity
import bangkit.mobiledev.storyappdicoding.database.room.StoryDatabase
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val sortOrder: String = "createdAt"
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val token = "Bearer ${userPreferences.getToken().first()}"
            val response = apiService.getAllStories(token, page, state.config.pageSize, sort = sortOrder)

            val endOfPaginationReached = response.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().clearAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = response.listStory.map {
                    RemoteKeysEntity(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                database.remoteKeysDao().insertAll(remoteKeys)
                database.storyDao().insertStories(
                    response.listStory.map { story ->
                        StoryEntity(
                            id = story.id,
                            name = story.name,
                            description = story.description,
                            photoUrl = story.photoUrl,
                            createdAt = story.createdAt,
                            lat = story.lat as? Double,
                            lon = story.lon as? Double
                        )
                    }
                )
            }

            return MediatorResult.Success(endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

}