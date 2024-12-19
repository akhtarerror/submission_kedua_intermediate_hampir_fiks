package bangkit.mobiledev.storyappdicoding.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _pagingStories: LiveData<PagingData<StoryEntity>>? = null

    fun getPagingStories(): LiveData<PagingData<StoryEntity>> {
        if (_pagingStories == null) {
            _pagingStories = storyRepository.getPagingStory().cachedIn(viewModelScope)
        }
        return _pagingStories!!
    }

    fun logout() = userRepository.logout()
}
