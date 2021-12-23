package ro.andreea.bolonyi.todolist.domain

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.LocalDate

class MyConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToMyDate(string: String): LocalDate? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, LocalDate::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun myDateToString(myDate: LocalDate?): String {
        return Gson().toJson(myDate)
    }
}