package ro.andreea.bolonyi.todolist.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import ro.andreea.bolonyi.todolist.domain.Task

@Dao
interface ITasksRepository {

    @Query("select * from tasks inner join userstasks on userId= :currentUserId where userstasks.taskId= tasks.taskId")
    fun getAllTasksForCurrentUser(currentUserId: Int?): LiveData<MutableList<Task>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(task: Task): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(task: Task): Int

    @Query("delete from tasks where taskId= :taskId")
    fun delete(taskId: Int)

    @Query("select max(taskId) from tasks")
    fun getLastId(): Int
}