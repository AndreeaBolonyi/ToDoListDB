package ro.andreea.bolonyi.todolist.domain

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson

class MyConverter {
    @TypeConverter
    fun stringToMyDate(string: String): MyDate? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, MyDate::class.java)
    }

    @TypeConverter
    fun myDateToString(myDate: MyDate?): String {
        return Gson().toJson(myDate)
    }
}