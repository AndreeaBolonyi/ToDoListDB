package ro.andreea.bolonyi.todolist.fragments

import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import ro.andreea.bolonyi.todolist.R
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.domain.User
import ro.andreea.bolonyi.todolist.viewmodel.TaskViewModel
import java.time.LocalDate

class TaskDetailsDeleteFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel

    private fun parseUsersToString(users: List<User>): String {
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseLocalDateToString(date: LocalDate?): String {
        return "${date?.dayOfMonth}.${date?.monthValue}.${date?.year}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.task_details_for_delete, container, false)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            val builder = AlertDialog.Builder(this.context)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(context, "Please wait, server is processing your request", Toast.LENGTH_LONG).show()
                    Utils.selectedTask!!.taskId?.let { it1 -> taskViewModel.delete(it1) }
                    Log.d("tasksDeleteFragment", "deleted ${Utils.selectedTask.toString()}")
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        taskViewModel.mutableSaved.observe(viewLifecycleOwner, {deleted ->
            if (deleted) {
                Toast.makeText(context, "Successful!.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        })

        taskViewModel.mutableError.observe(viewLifecycleOwner, {error ->
            if(error != "") {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })

        setEdiTexts(view)

        return view
    }
}
