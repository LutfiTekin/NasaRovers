package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.paging.RoverPhotoComparator

class PhotosAdapter : PagingDataAdapter<Photo, RoverPhotoViewHolder>(RoverPhotoComparator) {

    override fun onBindViewHolder(holder: RoverPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoverPhotoViewHolder {
        return RoverPhotoViewHolder(
            LayoutInflater.from(parent.context),
            parent,
            R.layout.list_item_photo
        )
    }
}
