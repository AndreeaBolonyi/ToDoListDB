package ro.andreea.bolonyi.todolist.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "userId")
    var userId: Int?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "gitHubUsername")
    val gitHubUsername: String?,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "password")
    val password: String?,
) {
    override fun toString(): String {
        return "$name $gitHubUsername $email"
    }
}