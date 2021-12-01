package ro.andreea.bolonyi.todolist.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ro.andreea.bolonyi.todolist.domain.User

@Dao
interface IUsersRepository {

    @Query("select * from users where email= :email and password= :password")
    fun findUserByEmailAndPassword(email: String, password: String): User?

    @Query("select * from users where gitHubUsername= :gitHubUsername")
    fun findByGitHubUsername(gitHubUsername: String): User?

    @Query("select * from users where userId= :userId")
    fun getUserById(userId: Int?): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(user: User): Long

    @Query("select max(userId) from users")
    fun getLastId(): Int
}