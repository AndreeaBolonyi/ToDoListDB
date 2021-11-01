package ro.andreea.bolonyi.todolist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ro.andreea.bolonyi.todolist.RecyclerViewAdapter
import ro.andreea.bolonyi.todolist.R
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.domain.Task
import ro.andreea.bolonyi.todolist.viewmodel.TaskViewModel
import java.util.*

class MainPageFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.main_page, container, false)
        adapter = RecyclerViewAdapter()
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.tasksList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)


        Log.d("initial tasks state", "${taskViewModel.allTasks.value?.size}")
        taskViewModel.allTasks.observe(viewLifecycleOwner, { tasks ->
            tasks?.let { adapter.setTasks(tasks) }
        })

        view.findViewById<Button>(R.id.addButton).setOnClickListener{
            Utils.selectedTask = null
            findNavController().navigate(R.id.taskDetailsFragment)
            adapter.currentItem = null
        }

        view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            val selectedTask: Task? = Utils.selectedTask

            if (selectedTask == null) {
                Toast.makeText(this.context, "Please select a task", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            findNavController().navigate(R.id.taskDetailsDeleteFragment)
            adapter.currentItem = null
        }

        view.findViewById<Button>(R.id.updateButton).setOnClickListener {
            val selectedTask: Task? = Utils.selectedTask

            if (selectedTask == null) {
                Toast.makeText(this.context, "Please select a task", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            findNavController().navigate(R.id.taskDetailsFragment)
            adapter.currentItem = null
        }

        return view
    }
}
