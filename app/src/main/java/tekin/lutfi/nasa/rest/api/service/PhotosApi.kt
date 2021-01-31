package tekin.lutfi.nasa.rest.api.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.toConsole

class PhotosApi(private val rover: String, private val retrofit: Retrofit, private val camera: String?) {

    var page = 0

    suspend fun loadPhotos(requestedPage: Int): List<Photo>{
        try {
            page = requestedPage
            val service = retrofit.create(MarsRoverPhotosService::class.java)
            val response = service.roverPhotosBySol(rover,1000,camera,page)
            val type = object : TypeToken<List<Photo>>(){}.type
            val body = response.body()?.get("photos")
            val list = Gson().fromJson<List<Photo>>(body,type)
            /*val map = dataSource.value?.toMutableMap() ?: mutableMapOf()
            val currentList = map[rover] ?: listOf()
            val newList = currentList.toMutableList().apply {
                addAll(list)
            }
            map[rover] = newList
                .toSet()//To ensure there are no duplicates
                .toList()
            dataSource.postValue(map)*/
            return list
        } catch (e: Exception) {
            e.message.toConsole()
        }
        return emptyList()
    }

}