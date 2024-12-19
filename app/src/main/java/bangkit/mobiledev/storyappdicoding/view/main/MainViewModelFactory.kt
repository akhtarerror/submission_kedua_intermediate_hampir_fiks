package bangkit.mobiledev.storyappdicoding.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository

class MainViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
