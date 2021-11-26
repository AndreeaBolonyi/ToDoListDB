package ro.andreea.bolonyi.todolist.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "userstasks",
    primaryKeys = ["userId", "taskId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"]),
        ForeignKey(entity = Task::class, parentColumns = ["taskId"], childColumns = ["taskId"])
    ],
    indices = [
        Index("userId"),
        Index("taskId")
    ]
)
data class UserTask (
    @ColumnInfo(name = "userId")
    var userId: Int,

    @ColumnInfo(name = "taskId")
    val taskId: Int
)