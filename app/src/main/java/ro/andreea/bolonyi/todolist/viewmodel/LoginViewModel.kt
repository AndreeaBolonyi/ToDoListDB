package ro.andreea.bolonyi.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.User

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoginResult = MutableLiveData<User>()
    val loginResult: LiveData<User> = mutableLoginResult

    init {
        Utils.setUsersRepository(application, viewModelScope)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userFound: User? = Utils.usersRepository.findUserByEmailAndPassword(email, password)
            Log.d("loginPage", "userFound: $userFound")
            mutableLoginResult.postValue(userFound)
        }
    }
}