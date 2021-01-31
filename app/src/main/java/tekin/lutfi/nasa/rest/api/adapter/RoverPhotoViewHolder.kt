package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.toConsole

class RoverPhotoViewHolder internal constructor(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes res: Int
) : RecyclerView.ViewHolder(inflater.inflate(res, parent, false)){

    private val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageView)

    fun bind(photo: Photo?){
        "loaded photo ${photo?.imgSrc}".toConsole()
        imageView.load(photo?.imgSrc)
    }

}