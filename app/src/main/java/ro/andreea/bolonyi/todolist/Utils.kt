package ro.andreea.bolonyi.todolist

import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.repository.TasksRepository
import ro.andreea.bolonyi.todolist.repository.TasksRepositoryImpl
import ro.andreea.bolonyi.todolist.repository.UsersRepository
import ro.andreea.bolonyi.todolist.repository.UsersRepositoryImpl

class Utils {
    companion object {
        var currentUser: User? = null
        val usersRepository: UsersRepository = UsersRepositoryImpl()
        val tasksRepository: TasksRepository = TasksRepositoryImpl()
        var selectedTask: Task? = null
    }
}
