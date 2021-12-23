package ro.andreea.bolonyi.todolist.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ro.andreea.bolonyi.todolist.domain.User

object UsersApi {
    private const val URL = "http://10.0.2.2:8080/to-do-list/users/"

    interface UsersService {
        @GET("http://10.0.2.2:8080/to-do-list/users/github-username/{gitHubUsername}")
        suspend fun getUserByGitHubUsername(@Path("gitHubUsername") gitHubUsername: String): User

        @POST("http://10.0.2.2:8080/to-do-list/users/login")
        suspend fun getUserByEmailAndPassword(@Body user: User): User

        @GET("http://10.0.2.2:8080/to-do-list/users/{userId}")
        suspend fun getUserById(@Path("userId") userId: Int): User

        @POST("http://10.0.2.2:8080/to-do-list/users/new-user")
        suspend fun addUser(@Body user: User): Int
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

    val service: UsersService = retrofit.create(UsersService::class.java)
}