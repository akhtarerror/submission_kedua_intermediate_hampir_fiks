package bangkit.mobiledev.storyappdicoding.data.repository

import bangkit.mobiledev.storyappdicoding.data.response.ErrorResponse
import bangkit.mobiledev.storyappdicoding.data.response.LoginResponse
import bangkit.mobiledev.storyappdicoding.data.response.RegisterResponse
import bangkit.mobiledev.storyappdicoding.data.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class UserRepository(
    private val apiService: ApiService
) {

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = apiService.login(email, password)
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse? {
        return try {
            val response = apiService.register(name, email, password)
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}
