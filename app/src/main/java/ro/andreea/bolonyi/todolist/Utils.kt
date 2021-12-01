package ro.andreea.bolonyi.todolist

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.repository.*

class Utils {
    companion object {
        var currentUser: User? = null
        lateinit var usersRepository: UsersRepositoryImpl
        lateinit var tasksRepository: TasksRepositoryImpl
        var selectedTask: Task? = null
        var shouldInitUsersRepository = true

        fun setUsersRepository(application: Application, scope: CoroutineScope) {
            val usersRepoDB = MyRoomDatabase.getDatabase(application, scope).usersRepoDB()
            usersRepository = UsersRepositoryImpl(usersRepoDB)
            shouldInitUsersRepository = false
        }

        fun setTasksRepository(application: Application, scope: CoroutineScope) {
            val tasksRepoDB = MyRoomDatabase.getDatabase(application, scope).tasksRepoDB()
            val userstasksRepoDB = MyRoomDatabase.getDatabase(application, scope).userstasksRepoDB()
            tasksRepository = TasksRepositoryImpl(tasksRepoDB, userstasksRepoDB)
        }
    }
}
