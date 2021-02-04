package tekin.lutfi.nasa.rest.api.rest.data

import android.content.Context
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import tekin.lutfi.nasa.rest.api.rest.model.Photo
import tekin.lutfi.nasa.rest.api.paging.PAGE_SIZE
import tekin.lutfi.nasa.rest.api.rest.service.MarsRoverPhotosService
import tekin.lutfi.nasa.rest.api.util.*
import java.text.SimpleDateFormat
import java.util.*

const val DEFAULT_SOL = 1

class PhotosApi(private val rover: String, private val context: Context, private val camera: String?) {

    var page = 1
        set(value) {
            logPage(value.toLong())
            field = value
        }
    var selectedSol = 0

    private val maxSolKey = MAX_SOL + rover

    private val localData by lazy { LocalData(context) }

    private val service = context.defaultRetrofit.create(MarsRoverPhotosService::class.java)

    suspend fun findMostRecentList(): List<Photo>{
        return withContext(Dispatchers.IO){
            var maxRetry = 100
            if (localData.exists(maxSolKey)){
                maxRetry = localData.read(maxSolKey, maxRetry)
            }

            val combinedList = mutableListOf<Photo>()
            while (maxRetry > 0){
                maxRetry--
                "Retries left $maxRetry".toConsole(true)
                try {
                    val list = loadPhotos(1,true)
                    if (list.isNotEmpty()) {
                        combinedList.addAll(list)
                        if (combinedList.size >= PAGE_SIZE)
                            return@withContext combinedList
                    }
                } catch (e: Exception) {}
            }
            return@withContext emptyList()
        }
    }

    suspend fun loadPhotos(requestedPage: Int = 1, solConsumed: Boolean = false): List<Photo> {
        try {
            if (selectedSol == 0) {
                selectedSol = findMostRecentSol()
            }
            if (solConsumed){
                selectedSol--
                if (selectedSol == 0){
                    return emptyList()
                }
            }
            page = requestedPage
            val response = service.roverPhotosBySol(rover, selectedSol, camera, page)
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
    private suspend fun findMostRecentSol(): Int {
        if (localData.exists(maxSolKey)) {
            return localData.read(maxSolKey, DEFAULT_SOL)
        }
        val retryLimit = 5
        var currentDay = 0
        while (currentDay < retryLimit + 1) {
            //Get a date to try in millisecond unit
            val dayInMillis = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -currentDay)
            }.time.time
            //format date
            val stringDate = sdf.format(dayInMillis).also { it.toConsole() }
            try {
                //Query api with selected rover and camera
                val response = service.roverPhotosByDay(rover, stringDate, camera)
                val list = response.parseList()
                if (list.isNullOrEmpty())
                    throw Exception("Trying to find recent list $currentDay")
                return list[0].sol ?: throw Exception()
            } catch (e: Exception) {
                if (currentDay == retryLimit){
                    "Possible inactive rover ($rover) pulling manifest".toConsole()
                    return getMaxSol()
                }
                e.toConsole()
            }
            currentDay++
        }
        return DEFAULT_SOL
    }



    /**
     * Get max sol from the manifest
     */
    private suspend fun getMaxSol(): Int{
        try {
            val title = MAX_SOL + rover
            val localData = LocalData(context)
            if (localData.exists(title))
                return localData.read(MAX_SOL,-1)
            val response = liveRetrofit.create(MarsRoverPhotosService::class.java)
                .getRoverManifest(rover).body()?.get("photo_manifest")?.asJsonObject
            "$rover max sol ${response?.get(MAX_SOL)}".toConsole()
            return response?.get(MAX_SOL)?.asInt?.also {
                localData.write(title,it)
            } ?: throw Exception()
        } catch (e: Exception) {
            e.toConsole()
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

    private fun logPage(requestedPage: Long) {
        Firebase.analytics.logEvent("pagination") {
            param("rover", rover)
            param("camera", camera ?: "all")
            param("sol",selectedSol.toLong())
            param("page", requestedPage)
        }
    }

}