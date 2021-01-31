package tekin.lutfi.nasa.rest.api.model

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
        val curiosity = listOf("FHAZ", "RHAZ", "MAST", "CHEMCAM", "MAHLI", "MARDI", "NAVCAM")
        val opportunity = listOf("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES")
        val spirit = listOf("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES")
    }

}