package tekin.lutfi.nasa.rest.api.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tekin.lutfi.nasa.rest.api.defaultRetrofit
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.service.MarsRoverPhotosService
import tekin.lutfi.nasa.rest.api.toConsole



class RoverViewModel : ViewModel() {

    val filterByCamera = MutableLiveData<Map<String,String>>()

}