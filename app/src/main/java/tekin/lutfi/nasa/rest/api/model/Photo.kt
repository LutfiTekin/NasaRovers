package tekin.lutfi.nasa.rest.api.model

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
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
) {

    fun logDetail() {
        Firebase.analytics.logEvent("photo-detail-selected") {
            param("rover", rover?.name.orEmpty())
            param("camera", camera?.name.orEmpty())
            param("id", (id ?: 0).toLong())
            param("sol", (sol ?: 0).toLong())
        }
    }

}