package ro.andreea.bolonyi.todolist

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ro.andreea.bolonyi.todolist.domain.Task
import java.time.format.DateTimeFormatter

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var tasks: List<Task> = emptyList()
    var currentItem: View? = null

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_view, parent, false)

        return ViewHolder(itemView)
    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    override fun getItemCount() = tasks.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.taskTitle).text = tasks[position].title
        holder.itemView.findViewById<TextView>(R.id.taskDeadline).text = tasks[position].deadline.toString().format(DateTimeFormatter.ofPattern("dd-MMM-yy"))
        holder.itemView.findViewById<TextView>(R.id.taskCreated).text = tasks[position].created.toString().format(DateTimeFormatter.ofPattern("dd-MMM-yy"))
        holder.itemView.findViewById<TextView>(R.id.taskPriority).text = tasks[position].priority.toString()

        holder.itemView.setOnClickListener{
            Utils.selectedTask = tasks[position]
            holder.itemView.setBackgroundColor(Color.parseColor("#F9F0F7"))
            currentItem = holder.itemView
            Log.d("adapter", "selected task: ${Utils.selectedTask}")
        }
    }
}