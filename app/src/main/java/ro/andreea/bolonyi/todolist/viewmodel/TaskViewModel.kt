package ro.andreea.bolonyi.todolist.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.service.TasksApi
import java.lang.Exception

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    var mutableSaved = MutableLiveData<Boolean>().apply { value = false }
    var mutableError = MutableLiveData<String>().apply { value = "" }
    val lastInsertedTaskId = MutableLiveData<Int>().apply { value = -1 }
    var mutableAllTasks = MutableLiveData<List<Task>>().apply { value = emptyList() }

    private var lastInsertedTask: Task? = null
    val allTasks: LiveData<List<Task>> = mutableAllTasks

    fun initTasks() = viewModelScope.launch(Dispatchers.IO) {
        Utils.setTasksRepository(getApplication<Application>(), viewModelScope)
        //allTasks = Utils.tasksRepository.getAllTasksForCurrentUser()
        val tasks = TasksApi.service.getAllByUserId(Utils.currentUser?.userId!!)
        tasks.forEach{
            Log.d("taskViewMode", "$it")
        }
        mutableAllTasks.postValue(tasks)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun add(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "add $task")
        var isAdded = false
        try {
            //lastInsertedTaskId.postValue(Utils.tasksRepository.add(task).toInt()
            val errors = validateTask(task)
            if(errors != "") {
                mutableError.postValue(errors)
            }
            else {
                lastInsertedTaskId.postValue(TasksApi.service.addTask(task))
                lastInsertedTask = task
                if (lastInsertedTaskId.value != null) {
                    isAdded = true
                }
            }
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "add error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        catch(ex: HttpException) {
            Log.d("taskViewModel", "request was not approved(add), error code is ${ex.code()}")
            if (ex.code() == 400)
                mutableError.postValue("Please insert valid information")
            if(ex.code() == 500)
                mutableError.postValue("Server has an error")
        }
        catch (ex: Exception) {
            mutableError.postValue("Server is not available")
        }
        mutableSaved.postValue(isAdded)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "update $task")
        var isUpdated = false
        try {
            //isUpdated = Utils.tasksRepository.update(task)
            val errors = validateTask(task)
            if(errors != "") {
                mutableError.postValue(errors)
            }
            else {
                isUpdated = TasksApi.service.updateTask(task)
            }
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "update error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        catch(ex: HttpException) {
            Log.d("taskViewModel", "request was not approved(update), error code is ${ex.code()}")
            if (ex.code() == 400)
                mutableError.postValue("Please insert valid information")
            if(ex.code() == 500)
                mutableError.postValue("Server has an error")
        }
        catch (ex: Exception) {
            mutableError.postValue("Server is not available")
        }
        mutableSaved.postValue(isUpdated)
    }

    fun delete(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "delete task $taskId")
        var isDeleted = false
        try {
            //isDeleted = Utils.tasksRepository.delete(taskId)
            isDeleted = TasksApi.service.deleteTask(taskId)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "delete error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        catch(ex: HttpException) {
            Log.d("taskViewModel", "request was not approved(delete), error code is ${ex.code()}")
            if (ex.code() == 400)
                mutableError.postValue("Please insert valid information")
            if(ex.code() == 500)
                mutableError.postValue("Server has an error")
        }
        catch (ex: Exception) {
            mutableError.postValue("Server is not available")
        }
        mutableSaved.postValue(isDeleted)
    }

    fun setUsersToSelectedTask(taskId: Int?) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "set users to selected task with id $taskId")
        try {
            Utils.tasksRepository.setUsersToTask(taskId)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "set users to selected task error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        mutableSaved.postValue(true)
    }

    fun insertIntoUsersTasks() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "add into userstasks table")
        try {
            Log.d("taskViewModel", "last inserted task id $lastInsertedTask")
            if (lastInsertedTaskId.value != 0)
                lastInsertedTask?.let { Utils.tasksRepository.insertIntoUsersTasks(it) }
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "set users to selected task error: ${ex.message}")
            mutableError.postValue(ex.message)
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