package ro.andreea.bolonyi.todolist.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.MyDate
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import java.lang.Exception

interface TasksRepository {
    fun getAllTasksForCurrentUser(): LiveData<MutableList<Task>>
    fun add(task: Task): Boolean
    fun update(task: Task): Boolean
    fun delete(taskId: Int): Boolean
}

class TasksRepositoryImpl: TasksRepository {

    private var tasks: MutableList<Task> = mutableListOf()
    private var tasksForCurrentUser: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())

    init {
        initTasks(tasks)
    }

    override fun getAllTasksForCurrentUser(): LiveData<MutableList<Task>> {
        Log.d("tasksRepo", "tasks size: ${tasks.size}")
        tasks = filterByCurrentUser()
        tasksForCurrentUser.value = tasks
        return tasksForCurrentUser
    }

    override fun add(task: Task): Boolean {
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }

        Log.d("tasksRepo", "tasks size before add: ${tasksForCurrentUser.value?.size}")
        val nextId = getNextId()

        if(nextId != null) {
            task.id = nextId
            tasksForCurrentUser.value?.add(task)
            Log.d("tasksRepo", "tasks size after add: ${tasksForCurrentUser.value?.size}")
            return true
        }

        return false
    }

    override fun update(task: Task): Boolean {
        val errors = validateTask(task)
        if(errors != "") {
            Log.d("tasksRepo", errors)
            throw Exception(errors)
        }

        Utils.selectedTask = null
        for(t in tasksForCurrentUser.value!!) {
            if(t.id == task.id) {
                t.created = task.created
                t.deadline = task.deadline
                t.priority = task.priority
                t.title = task.title
                t.users = task.users
                return true
            }
        }

        return false
    }

    override fun delete(taskId: Int): Boolean {
        Utils.selectedTask = null
        Log.d("tasksRepo", "tasks size before delete: ${tasksForCurrentUser.value?.size}")
        for(t in tasksForCurrentUser.value!!) {
            if(t.id == taskId) {
                tasksForCurrentUser.value!!.remove(t)
                Log.d("tasksRepo", "tasks size after delete: ${tasksForCurrentUser.value?.size}")
                return true
            }
        }

        return false
    }

    private fun filterByCurrentUser(): MutableList<Task> {
        val newTasks: MutableList<Task> = mutableListOf()
        for(task in tasks) {
            if(task.users.contains(Utils.currentUser)) {
                newTasks.add(task)
            }
        }
        return newTasks
    }

    private fun getNextId(): Int? {
        return tasksForCurrentUser.value?.size?.plus(1)
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

fun initTasks(tasks: MutableList<Task>) {
    val user = User(1, "Andreea Bolonyi", "AndreeaBolonyi", "andreea@yahoo.com", "andreea")
    val user2 = User(2, "Flavius B", "FlaviusB", "flavius@yahoo.com", "flavius")
    tasks.add(Task(
        id = 1,
        title = "MA lab",
        deadline = MyDate(5, 10, 2021),
        priority = 1,
        created = MyDate(19, 10, 2021),
        users = List(1){user}
    ))
    tasks.add(Task(
        id = 2,
        title = "PPD lab",
        deadline = MyDate(26, 10, 2021),
        priority = 2,
        created = MyDate(11, 10, 2021),
        users = List(1){user}
    ))
    tasks.add(Task(
        id = 3,
        title = "LFTC lab",
        deadline = MyDate(18, 10, 2021),
        priority = 1,
        created = MyDate(11, 10, 2021),
        users = List(1){user}
    ))
    tasks.add(Task(
        id = 4,
        title = "Flavius to do item",
        deadline = MyDate(18, 10, 2021),
        priority = 1,
        created = MyDate(11, 10, 2021),
        users = List(1){user2}
    ))
}
