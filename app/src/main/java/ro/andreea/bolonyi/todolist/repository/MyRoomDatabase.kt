package ro.andreea.bolonyi.todolist.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.andreea.bolonyi.todolist.domain.*
import java.time.LocalDate

@Database(entities = [User::class, Task::class, UserTask::class], version = 2, exportSchema = false)
@TypeConverters(MyConverter::class)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun tasksRepoDB(): ITasksRepository
    abstract fun usersRepoDB(): IUsersRepository
    abstract fun userstasksRepoDB(): IUsersTasksRepository

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(MyRoomDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MyRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    //populateDatabase(database.usersRepoDB(), database.tasksRepoDB(), database.userstasksRepoDB())
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun populateDatabase(usersRepoDB: IUsersRepository, tasksRepoDB: ITasksRepository, userstasksRepoDB: IUsersTasksRepository) {
            val user1 = User(
                userId = 1,
                name = "Andreea Bolonyi",
                gitHubUsername = "AndreeaBolonyi",
                email = "andreea@yahoo.com",
                password = "andreea")
            val user2 = User(2, "Flavius B", "FlaviusB", "flavius@yahoo.com", "flavius")

            val task1 = Task(
                taskId = 1,
                title = "MA lab",
                deadline = LocalDate.of(2021, 10, 5),
                priority = 1,
                created = LocalDate.of(2021, 10, 19),
                users = List(1){user1}
            )

            val task2 = Task(
                taskId = 2,
                title = "PPD lab",
                deadline = LocalDate.of(2021, 10, 26),
                priority = 2,
                created = LocalDate.of(2021, 10, 11),
                users = List(1){user1}
            )

            val task3 = Task(
                taskId = 3,
                title = "LFTC lab",
                deadline = LocalDate.of(2021, 10, 18),
                priority = 1,
                created = LocalDate.of(2021, 10, 11),
                users = List(1){user1}
            )

            val task4 = Task(
                taskId = 4,
                title = "Flavius to do item",
                deadline = LocalDate.of(2021, 10, 18),
                priority = 1,
                created = LocalDate.of(2021, 10, 11),
                users = List(1){user2}
            )

            usersRepoDB.add(user1)
            usersRepoDB.add(user2)

            tasksRepoDB.add(task1)
            tasksRepoDB.add(task2)
            tasksRepoDB.add(task3)
            tasksRepoDB.add(task4)

            userstasksRepoDB.add(user1.userId, task1.taskId)
            userstasksRepoDB.add(user1.userId, task2.taskId)
            userstasksRepoDB.add(user1.userId, task3.taskId)
            userstasksRepoDB.add(user2.userId, task4.taskId)
        }
    }
}