package bangkit.mobiledev.storyappdicoding.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.StoryResponse
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _storiesWithMap = MutableLiveData<StoryResponse>()
    val storiesWithMap: LiveData<StoryResponse> = _storiesWithMap

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStoriesWithMap() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getAllStoriesWithMap()
                _storiesWithMap.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
}
