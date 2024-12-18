package bangkit.mobiledev.storyappdicoding.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.ListStoryItem
import bangkit.mobiledev.storyappdicoding.data.paging.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val stories: Flow<PagingData<ListStoryItem>> = Pager(
        config = PagingConfig(
            pageSize = 7, // Ukuran data yang diambil per halaman
            enablePlaceholders = false
        ),
        pagingSourceFactory = { StoryPagingSource(storyRepository) }
    ).flow.cachedIn(viewModelScope) // Meng-cache data di ViewModel
}
