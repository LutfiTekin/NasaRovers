package tekin.lutfi.nasa.rest.api.ui.fragment



class Spirit: BaseRover() {

    override val roverName = SPIRIT
    override val availableCameras =
        arrayOf("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES")

}