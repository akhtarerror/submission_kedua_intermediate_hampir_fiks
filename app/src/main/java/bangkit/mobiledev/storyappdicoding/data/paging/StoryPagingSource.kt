package bangkit.mobiledev.storyappdicoding.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.ListStoryItem

class StoryPagingSource(
    private val storyRepository: StoryRepository
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val currentPage = params.key ?: 1
            val response = storyRepository.getAllStories(page = currentPage, size = params.loadSize)
            LoadResult.Page(
                data = response.listStory,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.listStory.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
