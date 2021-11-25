package ro.andreea.bolonyi.todolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.User

class LoginViewModel : ViewModel() {
    private val mutableLoginResult = MutableLiveData<User>()
    val loginResult: LiveData<User> = mutableLoginResult

    fun login(email: String, password: String) {
        val userFound: User? = Utils.usersRepository.findUserByEmailAndPassword(email, password)
        Log.d("loginPage", "userFound: $userFound")
        mutableLoginResult.value = userFound
        Log.d("liveData", "${loginResult.value}")
    }
}