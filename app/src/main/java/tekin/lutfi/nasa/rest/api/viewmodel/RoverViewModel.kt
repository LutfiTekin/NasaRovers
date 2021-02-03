package tekin.lutfi.nasa.rest.api.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val MODE_LIST = "list"
const val MODE_GRID = "grid"

class RoverViewModel : ViewModel() {

    val launchFilterDialog = MutableLiveData<Boolean?>(null)

    /**
     * By default all available cameras are selected
     */
    val selectedCamera = MutableLiveData<Map<String,String>>()

    val recyclerViewMode = MutableLiveData<String>(MODE_LIST)

}