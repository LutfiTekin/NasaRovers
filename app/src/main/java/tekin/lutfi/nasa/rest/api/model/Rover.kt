package tekin.lutfi.nasa.rest.api.model

import com.google.gson.annotations.SerializedName

data class Rover(
    val id: Int? = null,
    @SerializedName("landing_date")
    val landingDate: String? = null,
    @SerializedName("launch_date")
    val launchDate: String? = null,
    val name: String? = null,
    val status: String? = null
)