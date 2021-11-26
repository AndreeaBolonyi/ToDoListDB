package ro.andreea.bolonyi.todolist.domain

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithUsers (
    @Embedded
    val task: Task,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "userId",
        associateBy = Junction(UserTask::class)
    )
    val users: List<User>
)