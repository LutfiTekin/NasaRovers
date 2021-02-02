package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo

class RoverPhotoViewHolder internal constructor(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes res: Int
) : RecyclerView.ViewHolder(inflater.inflate(res, parent, false)){

    private val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageView)
    private val camName = itemView.findViewById<AppCompatTextView>(R.id.camName)
    private var loadedPhoto: Photo? = null

    private val retryClickListener = View.OnClickListener {
        bind(loadedPhoto)
    }

    init {
        camName.setOnClickListener(retryClickListener)
    }

    fun bind(photo: Photo?){
        camName.isClickable = false
        camName.text = photo?.camera?.fullName.orEmpty()
        camName.isVisible = false
        loadedPhoto = photo
        imageView.load(photo?.imgSrc){
            crossfade(true)
            placeholder(R.drawable.ic_baseline_satellite_24)
            this.listener(object : ImageRequest.Listener{
                override fun onError(request: ImageRequest, throwable: Throwable) {
                    super.onError(request, throwable)
                    imageView.setImageResource(R.drawable.ic_baseline_error_outline_24)
                    camName.text = itemView.context.getString(R.string.image_load_error)
                    camName.isVisible = true
                    camName.isClickable = true
                }
                override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                    super.onSuccess(request, metadata)
                    camName.isVisible = true
                }
            })
        }
    }




}