package tekin.lutfi.nasa.rest.api.rest.model

import com.google.gson.annotations.SerializedName

data class Camera(
    @SerializedName("full_name")
    val fullName: String? = null,
    val id: Int? = null,
    val name: String = "",
    @SerializedName("rover_id")
    val roverId: Int? = null
){

    companion object {

        val list = listOf(
            "FHAZ",
            "RHAZ",
            "MAST",
            "CHEMCAM",
            "MAHLI",
            "MARDI",
            "NAVCAM",
            "PANCAM",
            "MINITES"
        )
    }

}