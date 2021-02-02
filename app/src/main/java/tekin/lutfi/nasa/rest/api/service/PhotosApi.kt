package tekin.lutfi.nasa.rest.api.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import retrofit2.Retrofit
import tekin.lutfi.nasa.rest.api.NASA_DATE_FORMAT
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.toConsole
import java.text.SimpleDateFormat
import java.util.*

class PhotosApi(private val rover: String, retrofit: Retrofit, private val camera: String?) {

    var page = 1
        set(value) {
            "Page requested $value".toConsole()
            field = value
        }
    var selectedSol = 0

    private val service = retrofit.create(MarsRoverPhotosService::class.java)

    suspend fun loadPhotos(requestedPage: Int): List<Photo>{
        try {
            if (selectedSol == 0){
                selectedSol = findMostRecentSol()
            }
            page = requestedPage
            val response = service.roverPhotosBySol(rover,selectedSol,camera,page)
            return response.parseList()
        } catch (e: Exception) {
            e.message.toConsole()
        }
        return emptyList()
    }

    /**
     * Find most recent available list
     * Rover photos aren't always up to date
     * From what I observed it is couple of days
     * behind from current date
     */
    private suspend fun findMostRecentSol(): Int{
        val retryLimit = 5
        var currentDay = 0
        while (currentDay < retryLimit){
            //Get a date to try in millisecond unit
            val dayInMillis = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR,-currentDay)
            }.time.time
            //format date
            val stringDate = sdf.format(dayInMillis).also { it.toConsole() }
            try {
                //Query api with selected rover and camera
                val response = service.roverPhotosByDay(rover, stringDate, camera)
                val list = response.parseList()
                if (list.isNullOrEmpty())
                    throw Exception("Trying to find recent list $currentDay")
                return list[0].sol ?: 1
            } catch (e: Exception) {
                e.toConsole()
            }
            currentDay++
        }
        return 1
    }

    private val sdf: SimpleDateFormat by lazy {
        val sdf = SimpleDateFormat(NASA_DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        sdf
    }

    private fun Response<JsonObject>.parseList(): List<Photo> {
        val type = object : TypeToken<List<Photo>>() {}.type
        val body = body()?.get("photos")
        return Gson().fromJson(body, type)
    }

}