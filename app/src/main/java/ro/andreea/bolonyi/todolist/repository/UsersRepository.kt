package ro.andreea.bolonyi.todolist.repository

import android.util.Log
import ro.andreea.bolonyi.todolist.domain.User

class UsersRepositoryImpl(private val usersRepoDB: IUsersRepository) {

    fun getUserById(userId: Int?): User {
        val userFound: User = usersRepoDB.getUserById(userId)
        Log.d("usersRepo", "user found by id: $userFound")
        return userFound
    }

    fun findUserByEmailAndPassword(email: String, password: String): User? {
        val userFound: User? = usersRepoDB.findUserByEmailAndPassword(email, password)
        Log.d("usersRepo", "user found by email and password: $userFound")
        return userFound
    }

    fun findByGitHubUsername(gitHubUsername: String): User? {
        val userFound: User? = usersRepoDB.findByGitHubUsername(gitHubUsername)
        Log.d("usersRepo", "user found by github username: $userFound")
        return userFound
    }
}