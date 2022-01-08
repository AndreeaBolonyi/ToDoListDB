package ro.andreea.bolonyi.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.service.UsersApi
import java.lang.Exception

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    val mutableUserId = MutableLiveData<Int>()
    val mutableError = MutableLiveData<String>().apply { value = "" }
    lateinit var newUser: User

    init {
        if(Utils.shouldInitUsersRepository)
            Utils.setUsersRepository(application, viewModelScope)
    }

    fun signup(name: String, gitHubUsername: String, email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            newUser = User(0, name, gitHubUsername, email, password)
            Log.d("signUpPage", "new user: $newUser")
            //mutableUserId.postValue(Utils.usersRepository.add(newUser).toInt())
            mutableUserId.postValue(UsersApi.service.addUser(newUser))
        }
        catch(ex: HttpException) {
            Log.d("loginViewModel", "request was not approved, error code is ${ex.code()}")
            if (ex.code() == 400)
                mutableError.postValue("Please insert valid information")
            if(ex.code() == 500)
                mutableError.postValue("Server has an error")
        }
        catch (ex: Exception) {
            mutableError.postValue("Server is not available")
        }
    }
}