package ro.andreea.bolonyi.todolist.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.domain.UserTask
import ro.andreea.bolonyi.todolist.service.UsersApi
import ro.andreea.bolonyi.todolist.service.UsersTasksApi
import java.lang.Exception

class TasksRepositoryImpl(private val tasksRepoDB: ITasksRepository, private val userstasksRepoDB: IUsersTasksRepository) {

    fun getAllTasksForCurrentUser(): LiveData<MutableList<Task>> {
        return tasksRepoDB.getAllTasksForCurrentUser(Utils.currentUser?.userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun add(task: Task): Long {
        Log.d("tasksRepo", "add task $task")
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }

        val taskId: Int = tasksRepoDB.getLastId() + 1
        task.taskId = taskId
        Log.d("tasksRepo", "add task $task")
        return tasksRepoDB.add(task)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(task: Task): Boolean {
        Log.d("tasksRepo", "update task $task")
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }
        Utils.selectedTask = null

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

    suspend fun setUsersToTask(taskId: Int?) {
        //val usersIds: List<Int> = userstasksRepoDB.getAllUsersIdForTaskId(taskId)
        val usersIds: List<Int> = taskId?.let { UsersTasksApi.service.getAllUsersIdForTaskId(it) }!!
        val users: MutableList<User> = mutableListOf()

        usersIds.forEach {
            //val user: User = Utils.usersRepository.getUserById(it)
            val user: User = UsersApi.service.getUserById(it)
            users.add(user)
        }

        Utils.selectedTask?.users = users
        Log.d("tasksRepo", "selected task has ${users.size} users")
    }

    suspend fun insertIntoUsersTasks(task: Task) {
        for(user: User in task.users) {
            //userstasksRepoDB.add(user.userId, task.taskId)
            if (user.userId != null && task.taskId != null) {
                val userTask = UserTask(user.userId!!, task.taskId!!)
                UsersTasksApi.service.add(userTask)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateTask(task: Task): String {
        var errors = ""

        if (task.deadline?.year!!.compareTo(task.created?.year!!) > 0) {
            errors += "Deadline should be after created\n"
        }

        if (task.deadline?.year!!.compareTo(task.created?.year!!) == 0 && task.deadline?.month!!.compareTo(task.created?.month!!) < 0) {
            errors += "Deadline should be after created\n"
        }

        if (task.deadline?.year!!.compareTo(task.created?.year!!) == 0 &&
                task.deadline?.month!!.compareTo(task.created?.month!!) == 0 &&
                    task.deadline?.dayOfMonth!!.compareTo(task.created?.dayOfMonth!!) < 0) {
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
