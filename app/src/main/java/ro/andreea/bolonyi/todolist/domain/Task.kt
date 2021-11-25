package ro.andreea.bolonyi.todolist.domain

data class Task (
    var id: Int = 0,
    var title: String?,
    var deadline: MyDate?,
    var created: MyDate?,
    var priority: Int?,
    var users: List<User>,
) {
    override fun toString(): String {
        return "$id $title $deadline $created $priority ${users.size}"
    }
}