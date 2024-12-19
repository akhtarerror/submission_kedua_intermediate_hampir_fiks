package bangkit.mobiledev.storyappdicoding.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.StoryResponse
import kotlinx.coroutines.launch

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _storiesWithLocation = MutableLiveData<StoryResponse>()
    val storiesWithLocation: LiveData<StoryResponse> = _storiesWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStoriesWithLocation() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getAllStoriesWithLocation()
                _storiesWithLocation.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
}