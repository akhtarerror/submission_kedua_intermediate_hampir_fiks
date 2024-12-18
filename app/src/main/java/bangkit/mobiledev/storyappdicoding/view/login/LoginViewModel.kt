package bangkit.mobiledev.storyappdicoding.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.mobiledev.storyappdicoding.data.pref.UserPreferences
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.data.response.LoginResponse

class LoginViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> get() = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    suspend fun login(email: String, password: String) {
        try {
            val result = userRepository.login(email, password)
            if (result != null && !result.error) {
                result.loginResult.token.let { token ->
                    userPreferences.saveToken(token)
                }
            }
            _loginResult.postValue(result)
        } catch (exception: Exception) {
            _errorMessage.postValue(exception.localizedMessage)
            _loginResult.postValue(null)
        }
    }

}
