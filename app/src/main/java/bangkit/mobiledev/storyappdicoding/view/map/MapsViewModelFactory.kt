package bangkit.mobiledev.storyappdicoding.view.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository

class MapViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}