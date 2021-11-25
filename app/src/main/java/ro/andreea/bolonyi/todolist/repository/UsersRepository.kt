package ro.andreea.bolonyi.todolist.repository

import android.util.Log
import ro.andreea.bolonyi.todolist.domain.User

interface UsersRepository {
    fun findUserByEmailAndPassword(email: String, password: String): User?
    fun findByGitHubUsername(gitHubUsername: String): User?
}

class UsersRepositoryImpl: UsersRepository {
    private val users: MutableList<User> = mutableListOf()

    init {
        users.add(
            User(
            id = 1,
            name = "Andreea Bolonyi",
            gitHubUsername = "AndreeaBolonyi",
            email = "andreea@yahoo.com",
            password = "andreea")
        )
        users.add(User(2, "Flavius B", "FlaviusB", "flavius@yahoo.com", "flavius"))
    }

    override fun findUserByEmailAndPassword(email: String, password: String): User? {
        val iterator = users.iterator()
        iterator.forEach {
            if(it.email.equals(email) && it.password.equals(password)) {
                Log.d("usersRepo", "credentials ok for user $it")
                return it
            }
        }
        return null
    }

    override fun findByGitHubUsername(gitHubUsername: String): User? {
        val iterator = users.iterator()
        iterator.forEach {
            if(it.gitHubUsername.equals(gitHubUsername)) {
                Log.d("usersRepo", "user found for gitHubUsername $gitHubUsername: $it")
                return it
            }
        }
        return null
    }
}