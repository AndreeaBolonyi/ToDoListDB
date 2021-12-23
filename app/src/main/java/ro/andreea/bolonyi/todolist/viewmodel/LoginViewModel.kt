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

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val mutableLoginResult = MutableLiveData<User>()
    val mutableError = MutableLiveData<String>().apply { value = "" }

    init {
        if(Utils.shouldInitUsersRepository)
            Utils.setUsersRepository(application, viewModelScope)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //val userFound: User? = Utils.usersRepository.findUserByEmailAndPassword(email, password)
                val userFound = UsersApi.service.getUserByEmailAndPassword(User(0, "", "", email, password))
                Log.d("loginPage", "userFound: $userFound")
                mutableLoginResult.postValue(userFound)
            }
            catch(ex: HttpException) {
                Log.d("loginViewModel", "request was not approved, error code is ${ex.code()}")
                if (ex.code() == 400)
                    mutableError.postValue("Please insert valid credentials")
                if(ex.code() == 500)
                    mutableError.postValue("Server has an error")
            }
        }
    }
}