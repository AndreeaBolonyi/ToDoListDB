package ro.andreea.bolonyi.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.User

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    val mutableUserId = MutableLiveData<Int>()
    lateinit var newUser: User

    init {
        if(Utils.shouldInitUsersRepository)
            Utils.setUsersRepository(application, viewModelScope)
    }

    fun signup(name: String, gitHubUsername: String, email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        newUser = User(0, name, gitHubUsername, email, password)
        Log.d("signUpPage", "new user: $newUser")
        mutableUserId.postValue(Utils.usersRepository.add(newUser).toInt())
    }
}