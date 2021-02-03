package tekin.lutfi.nasa.rest.api.rest.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarsRoverPhotosService {

    @GET("rovers/{rover}/photos")
    suspend fun roverPhotosBySol(
        @Path("rover") rover: String,
        @Query("sol") sol: Int,
        @Query("camera") camera: String?,
        @Query("page") page: Int
    ): Response<JsonObject>

    @GET("rovers/{rover}/photos")
    suspend fun roverPhotosByDay(
        @Path("rover") rover: String,
        @Query("earth_date") day: String,
        @Query("camera") camera: String?
    ): Response<JsonObject>

    @GET("manifests/{rover}")
    suspend fun getRoverManifest(
        @Path("rover") rover: String
    ): Response<JsonObject>



}