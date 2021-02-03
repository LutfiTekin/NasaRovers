package tekin.lutfi.nasa.rest.api.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val MAX_SOL = "max_sol"

class LocalData(private val context: Context) {

    private val default: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    fun write(title: String, content: String) = default.edit().putString(title, content).apply()

    fun write(title: String, content: Int) = default.edit().putInt(title, content).apply()

    fun read(title: String, defaultValue: String? = null): String? = default.getString(title, defaultValue)

    fun read(title: String, defaultValue: Int? = null): Int = default.getInt(title, defaultValue ?: -1)

    fun exists(title: String): Boolean = default.contains(title)

}