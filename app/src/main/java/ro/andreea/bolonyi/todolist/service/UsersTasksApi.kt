package ro.andreea.bolonyi.todolist.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ro.andreea.bolonyi.todolist.domain.UserTask

object UsersTasksApi {
    private const val URL = "http://10.0.2.2:8080/to-do-list/users-tasks/"

    interface UsersTasksService {
        @GET("http://10.0.2.2:8080/to-do-list/users-tasks/users-ids-for-task-id/{taskId}")
        suspend fun getAllUsersIdForTaskId(@Path("taskId") taskId: Int): List<Int>

        @POST("http://10.0.2.2:8080/to-do-list/users-tasks/new-user-task")
        suspend fun add(@Body userTask: UserTask)
    }

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val service: UsersTasksService = retrofit.create(UsersTasksService::class.java)
}