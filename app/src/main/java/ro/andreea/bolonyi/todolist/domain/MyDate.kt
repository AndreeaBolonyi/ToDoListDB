package ro.andreea.bolonyi.todolist.domain

data class MyDate (
    var day: Int,
    var month: Int,
    var year: Int
    ) {
    override fun toString(): String {
        return "$day.$month.$year"
    }
}
