package tekin.lutfi.nasa.rest.api.ui.fragment



class Opportunity: BaseRover() {

    override val roverName = OPPORTUNITY
    override val availableCameras =
        arrayOf("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES")

}