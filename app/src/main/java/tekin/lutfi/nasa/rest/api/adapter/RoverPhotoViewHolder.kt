package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
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
    listener: DetailListener,
    @LayoutRes res: Int
) : RecyclerView.ViewHolder(inflater.inflate(res, parent, false)){

    private val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageView)
    private val camName = itemView.findViewById<AppCompatTextView?>(R.id.camName)
    private var loadedPhoto: Photo? = null

    init {
        camName?.setOnClickListener { bind(loadedPhoto) }
        imageView.setOnClickListener { listener.onSelected(loadedPhoto) }
    }

    fun bind(photo: Photo?){
        imageView.isClickable = false
        camName?.apply {
            isClickable = false
            text = photo?.camera?.fullName.orEmpty()
            isVisible = false
        }
        loadedPhoto = photo
        imageView.load(photo?.imgSrc){
            crossfade(true)
            placeholder(R.drawable.ic_baseline_satellite_24)
            this.listener(coilRequestListener)
        }
    }

    private val coilRequestListener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, throwable: Throwable) {
            super.onError(request, throwable)
            imageView.apply {
                setImageResource(R.drawable.ic_baseline_error_outline_24)
                isClickable = false
            }
            camName?.apply {
                text = itemView.context.getString(R.string.image_load_error)
                isVisible = true
                isClickable = true
            }
        }

        override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
            super.onSuccess(request, metadata)
            camName?.apply {
                isVisible = true
                isClickable = false
            }
            imageView.isClickable = true
        }
    }




}