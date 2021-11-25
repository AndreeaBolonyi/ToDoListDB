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
    var allTasks: LiveData<MutableList<Task>> = Utils.tasksRepository.getAllTasksForCurrentUser()

    fun add(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "add $task")
        var isAdded = false
        try {
            isAdded = Utils.tasksRepository.add(task)
        }
        catch(ex: Exception) {
            Log.d("taskViewModel", "add error $ex")
            mutableError.postValue(ex.toString())
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
            Log.d("taskViewModel", "update error $ex")
            mutableError.postValue(ex.toString())
        }
        mutableSaved.postValue(isUpdated)
    }

    fun delete(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("taskViewModel", "delete task $taskId")
        mutableSaved.postValue(Utils.tasksRepository.delete(taskId))
    }
}