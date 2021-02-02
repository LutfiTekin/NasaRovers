package tekin.lutfi.nasa.rest.api.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo

const val ROVER_PHOTO = "rp"

class PhotoDetail: BottomSheetDialogFragment() {

    private var photo: Photo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.getString(ROVER_PHOTO)?.let { data ->
            val type = object : TypeToken<Photo>(){}.type
            photo = Gson().fromJson(data,type)
        }
        return inflater.inflate(R.layout.dialog_photo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val camera = photo?.camera ?: return
        val rover = photo?.rover ?: return
        with(view){
            findViewById<AppCompatTextView>(R.id.cameraName).text = camera.fullName
            findViewById<AppCompatTextView>(R.id.photoDate).text = photo?.earthDate
            findViewById<AppCompatTextView>(R.id.roverName).text = with(rover){"$name ($status)"}
            findViewById<AppCompatTextView>(R.id.launchDate).text = rover.launchDate
            findViewById<AppCompatTextView>(R.id.landingDate).text = rover.landingDate
            findViewById<AppCompatImageView>(R.id.imageView).load(photo?.imgSrc)
        }
    }

}