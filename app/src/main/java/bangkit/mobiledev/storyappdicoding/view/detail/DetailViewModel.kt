package bangkit.mobiledev.storyappdicoding.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: LiveData<Story> get() = _storyDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getDetailStory(token: String, storyId: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getDetailStory(token, storyId)
                _storyDetail.value = response.story
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "An unknown error occurred"
            }
        }
    }
}
