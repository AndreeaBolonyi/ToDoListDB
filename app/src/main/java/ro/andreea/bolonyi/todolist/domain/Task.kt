package ro.andreea.bolonyi.todolist.domain

import androidx.room.*

@Entity(tableName = "tasks")
class Task {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "taskId")
    var taskId: Int? = 0

    @ColumnInfo(name = "title")
    var title: String?

    @ColumnInfo(name = "deadline")
    var deadline: MyDate?

    @ColumnInfo(name = "created")
    var created: MyDate?

    @ColumnInfo(name = "priority")
    var priority: Int?

    @Ignore
    var users: List<User>

    constructor(title: String?, deadline:MyDate?, created: MyDate?, priority: Int?, users: List<User>) {
        this.title = title
        this.deadline = deadline
        this.created = created
        this.priority = priority
        this.users = users
    }

    constructor(taskId: Int?, title: String?, deadline:MyDate?, created: MyDate?, priority: Int?) {
        this.taskId = taskId
        this.title = title
        this.deadline = deadline
        this.created = created
        this.priority = priority
        this.users = mutableListOf()
    }

    constructor(taskId: Int?, title: String?, deadline:MyDate?, created: MyDate?, priority: Int?, users: List<User>) {
        this.taskId = taskId
        this.title = title
        this.deadline = deadline
        this.created = created
        this.priority = priority
        this.users = users
    }

    override fun toString(): String {
        return "$taskId $title $deadline $created $priority ${users.size}"
    }
}