package ro.andreea.bolonyi.todolist.domain

data class User(
    val id: Int,
    val name: String? = null,
    val gitHubUsername: String? = null,
    val email: String? = null,
    val password: String? = null
) {
    override fun toString(): String {
        return "$name $gitHubUsername $email"
    }
}