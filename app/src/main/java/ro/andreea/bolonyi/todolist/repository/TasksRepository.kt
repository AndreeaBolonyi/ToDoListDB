package ro.andreea.bolonyi.todolist.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import java.lang.Exception

class TasksRepositoryImpl(private val tasksRepoDB: ITasksRepository, private val userstasksRepoDB: IUsersTasksRepository) {

    fun getAllTasksForCurrentUser(): LiveData<MutableList<Task>> {
        return tasksRepoDB.getAllTasksForCurrentUser(Utils.currentUser?.userId)
    }

    fun add(task: Task): Boolean {
        Log.d("tasksRepo", "add task $task")
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }

        val generatedId: Long = tasksRepoDB.add(task)
        for(user: User in task.users)
            userstasksRepoDB.add(user.userId, generatedId.toInt())

        Log.d("tasksRepo", "id for the inserted task is $generatedId")
        if (generatedId > 0)
            return true
        return false
    }

    fun update(task: Task): Boolean {
        Log.d("tasksRepo", "update task $task")
        Utils.selectedTask = null
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }

        if (tasksRepoDB.update(task) > 0)
            return true
        return false
    }

    fun delete(taskId: Int): Boolean {
        Log.d("tasksRepo", "delete task with id $taskId")
        Utils.selectedTask = null

        userstasksRepoDB.delete(taskId)
        tasksRepoDB.delete(taskId)
        return true
    }

    fun setUsersToTask(taskId: Int?) {
        val usersIds: List<Int> = userstasksRepoDB.getAllUsersIdForTaskId(taskId)
        val users: MutableList<User> = mutableListOf()

        usersIds.forEach {
            val user: User = Utils.usersRepository.getUserById(it)
            users.add(user)
        }

        Utils.selectedTask?.users = users
        Log.d("tasksRepo", "selected task has ${users.size} users")
    }

    private fun validateTask(task: Task): String {
        var errors = ""

        if (task.deadline?.year!! < task.created?.year!!) {
            errors += "Deadline should be after created\n"
        }

        if (task.deadline?.year!! == task.created?.year!! && task.deadline?.month!! < task.created?.month!!) {
            errors += "Deadline should be after created\n"
        }

        if (task.deadline?.year!! == task.created?.year!! &&
                task.deadline?.month!! == task.created?.month!! &&
                    task.deadline?.day!! < task.created?.day!!) {
            errors += "Deadline should be after created\n"
        }

        if (task.title == "") {
            errors += "Title cannot be empty\n"
        }

        if (task.users.isEmpty()) {
            errors += "Task should be assigned to an user\n"
        }

        return errors
    }
}
