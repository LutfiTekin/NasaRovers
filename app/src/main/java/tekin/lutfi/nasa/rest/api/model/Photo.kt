package tekin.lutfi.nasa.rest.api.model

import com.google.gson.annotations.SerializedName

data class Photo(
    val camera: Camera? = null,
    @SerializedName("earth_date")
    val earthDate: String? = null,
    val id: Int? = null,
    @SerializedName("img_src")
    val imgSrc: String? = null,
    val rover: Rover? = null,
    val sol: Int? = null
)