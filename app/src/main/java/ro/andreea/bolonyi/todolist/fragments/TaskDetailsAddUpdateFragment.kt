package ro.andreea.bolonyi.todolist.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.andreea.bolonyi.todolist.R
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.MyDate
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.service.UsersApi
import ro.andreea.bolonyi.todolist.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime

class TaskDetailsAddUpdateFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel

    private suspend fun getUsersFromEditText(text: String): List<User> {
        Log.d("tasksFragment", "getUsers $text")

        if (!text.contains(" ")) {
            //val userFound: User? = Utils.usersRepository.findByGitHubUsername(text)
            val userFound: User = UsersApi.service.getUserByGitHubUsername(text)
            if(userFound.userId != null) {
                return List(1){userFound}
            }
            return emptyList()
        }

        val users: MutableList<User> = mutableListOf()
        val aux = text.split(" ")

        for(t in aux) {
            if(t != "") {
                //val userFound: User? = Utils.usersRepository.findByGitHubUsername(t)
                val textWithoutSpaces = text.replace(" " , "")
                val userFound: User = UsersApi.service.getUserByGitHubUsername(textWithoutSpaces)

                if(userFound.userId != null) {
                    users.add(userFound)
                }
                else {
                    throw Exception("Invalid GitHub username")
                }
            }
        }

        return users
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseLocalDateToString(date: LocalDate?): String {
        return "${date?.dayOfMonth}.${date?.monthValue}.${date?.year}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateFromString(text: String): LocalDate {
        Log.d("tasksFragment", "parseDate $text")

        if(!text.contains("."))
            throw java.lang.Exception("invalid format for date")
        else {
            val aux = text.split(".")
            if (aux.size != 3)
                throw java.lang.Exception("invalid format for date")
            else {
                val day = Integer.parseInt(aux[0])
                val month = Integer.parseInt(aux[1])
                val year = Integer.parseInt(aux[2])

                if (day < 1 || day > 31)
                    throw java.lang.Exception("Invalid day")

                if (month < 1 || month > 12)
                    throw java.lang.Exception("Invalid month")

                return LocalDate.of(year, month, day)
            }
        }
    }

    private fun parseUsersToString(users: List<User>): String {
        Log.d("tasksFragment", "parse users to string")
        var text = ""

        for(user in users) {
            text += user.gitHubUsername
            text += " "
        }

        return text
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEdiTexts(view: View) {
        val selectedTask: Task? = Utils.selectedTask
        if (selectedTask != null) {
            view.findViewById<EditText>(R.id.editTextUsers).setText(parseUsersToString(selectedTask.users), TextView.BufferType.EDITABLE)
            view.findViewById<EditText>(R.id.editTextTitle).setText(selectedTask.title, TextView.BufferType.EDITABLE)
            view.findViewById<EditText>(R.id.editTextDeadline).setText(parseLocalDateToString(selectedTask.deadline), TextView.BufferType.EDITABLE)
            view.findViewById<EditText>(R.id.editTextCreated).setText(parseLocalDateToString(selectedTask.created), TextView.BufferType.EDITABLE)
            view.findViewById<EditText>(R.id.editTextPriority).setText(selectedTask.priority.toString(), TextView.BufferType.EDITABLE)
        }
        else {
            val currentDateTime = LocalDateTime.now()
            val today = MyDate(currentDateTime.dayOfMonth, currentDateTime.monthValue, currentDateTime.year)

            val created = view.findViewById<EditText>(R.id.editTextCreated)
            created.setText(today.toString(), TextView.BufferType.EDITABLE)
            created.isEnabled = false

            view.findViewById<EditText>(R.id.editTextUsers).setText(Utils.currentUser?.gitHubUsername)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.task_details_add_update, container, false)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (Utils.selectedTask == null) {
                    addTask(view)
                }
                else {
                    updateTask(view)
                }
            }
        }

        taskViewModel.mutableSaved.observe(viewLifecycleOwner, {saved ->
            if (saved) {
                Toast.makeText(context, "Successful!.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        })

        taskViewModel.mutableError.observe(viewLifecycleOwner, {error ->
            if(error != "") {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })

        taskViewModel.lastInsertedTaskId.observe(viewLifecycleOwner, {taskId ->
            if(taskId != 0) {
                Log.d("addUpdateFragment", "last inserted task id is $taskId")
                taskViewModel.insertIntoUsersTasks()
            }
        })

        setEdiTexts(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun addTask(view: View) {
        var task: Task? = null
        try {
            task = Task(
                title = view.findViewById<EditText>(R.id.editTextTitle).text.toString(),
                deadline = parseDateFromString(view.findViewById<EditText>(R.id.editTextDeadline).text.toString()),
                created = parseDateFromString(view.findViewById<EditText>(R.id.editTextCreated).text.toString()),
                priority = Integer.parseInt(view.findViewById<EditText>(R.id.editTextPriority).text.toString()),
                users = getUsersFromEditText(view.findViewById<EditText>(R.id.editTextUsers).text.toString())
            )
        } catch (ex: NumberFormatException) {
            Log.d("addUpdateFragment", "${ex.message}")
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Invalid priority. It should be a number", Toast.LENGTH_LONG).show()
            }
        } catch (ex: java.lang.Exception) {
            Log.d("addUpdateFragment", "${ex.message}")
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            }
        }

        if (task != null) {
            Log.d("tasksFragment", "task to add: $task")
            try {
                taskViewModel.add(task)
            }
            catch (ex: java.lang.Exception) {
                Log.d("addUpdateFragment", "${ex.message}")
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateTask(view: View) {
        var task: Task? = null
        try {
            task = Utils.selectedTask?.let {
                Task(
                    title = view.findViewById<EditText>(R.id.editTextTitle).text.toString(),
                    deadline = parseDateFromString(view.findViewById<EditText>(R.id.editTextDeadline).text.toString()),
                    created = parseDateFromString(view.findViewById<EditText>(R.id.editTextCreated).text.toString()),
                    priority = Integer.parseInt(view.findViewById<EditText>(R.id.editTextPriority).text.toString()),
                    users =  getUsersFromEditText(view.findViewById<EditText>(R.id.editTextUsers).text.toString()),
                    taskId = it.taskId
                )
            }

        } catch (ex: NumberFormatException) {
            Log.d("addUpdateFragment", "${ex.message}")
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Invalid priority. It should be a number", Toast.LENGTH_LONG).show()
            }
        }
        catch(ex: java.lang.Exception) {
            Log.d("addUpdateFragment", "${ex.message}")
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }

        if (task != null) {
            Log.d("tasksFragment", "task to update: $task")
            try {
                taskViewModel.update(task)
            }
            catch (ex: java.lang.Exception) {
                Log.d("addUpdateFragment", "${ex.message}")
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
