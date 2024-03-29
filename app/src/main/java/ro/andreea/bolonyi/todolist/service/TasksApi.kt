package ro.andreea.bolonyi.todolist.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ro.andreea.bolonyi.todolist.domain.Task
import java.time.LocalDate

object TasksApi {
    private const val URL = "http://10.0.2.2:8080/to-do-list/tasks/"

    interface TasksService {
        @GET("http://10.0.2.2:8080/to-do-list/tasks/tasks-for-user/{userId}")
        suspend fun getAllByUserId(@Path("userId") userId: Int): List<Task>

        @POST("http://10.0.2.2:8080/to-do-list/tasks/new-task")
        suspend fun addTask(@Body task: Task): Int

        @PUT("http://10.0.2.2:8080/to-do-list/tasks/update-task")
        suspend fun updateTask(@Body task: Task): Boolean

        @DELETE("http://10.0.2.2:8080/to-do-list/tasks/delete-task/{taskId}")
        suspend fun deleteTask(@Path("taskId") taskId: Int): Boolean
    }

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    @RequiresApi(Build.VERSION_CODES.O)
    private var gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
        .setLenient()
        .create()

    @RequiresApi(Build.VERSION_CODES.O)
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    val service: TasksService = retrofit.create(TasksService::class.java)
}