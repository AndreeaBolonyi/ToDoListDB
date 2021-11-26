package ro.andreea.bolonyi.todolist.repository

import androidx.room.Dao
import androidx.room.Query

@Dao
interface IUsersTasksRepository {
    @Query("insert into userstasks(userId, taskId) values (:userId, :taskId)")
    fun add(userId: Int?, taskId: Int?)

    @Query("delete from userstasks where taskId= :taskId")
    fun delete(taskId: Int?)

    @Query("select userId from userstasks where taskId= :taskId")
    fun getAllUsersIdForTaskId(taskId: Int?): List<Int>
}