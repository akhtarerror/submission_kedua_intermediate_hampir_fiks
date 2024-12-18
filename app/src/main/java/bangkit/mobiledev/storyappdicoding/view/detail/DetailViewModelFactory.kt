package bangkit.mobiledev.storyappdicoding.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository

class DetailViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}