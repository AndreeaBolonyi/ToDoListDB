package ro.andreea.bolonyi.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import java.lang.Exception

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    var mutableSaved = MutableLiveData<Boolean>().apply { value = false }
    var mutableError = MutableLiveData<String>().apply { value = "" }
    lateinit var allTasks: LiveData<MutableList<Task>>

    fun initTasks() {
        Utils.setTasksRepository(getApplication<Application>(), viewModelScope)
        allTasks = Utils.tasksRepository.getAllTasksForCurrentUser()
    }

    fun add(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "add $task")
        var isAdded = false
        try {
            isAdded = Utils.tasksRepository.add(task)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "add error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        mutableSaved.postValue(isAdded)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "update $task")
        var isUpdated = false
        try {
            isUpdated = Utils.tasksRepository.update(task)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "update error: ${ex.message}")
            mutableError.postValue(ex.message)
        }
        mutableSaved.postValue(isUpdated)
    }

    fun delete(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "delete task $taskId")
        var isDeleted = false
        try {
            isDeleted = Utils.tasksRepository.delete(taskId)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "delete error: ${ex.message}")
            mutableError.postValue(ex.message)
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
}