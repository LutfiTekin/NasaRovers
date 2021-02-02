package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.paging.RoverPhotoComparator

const val TYPE_LIST = 1
const val TYPE_GRID = 2

class PhotosAdapter(val listener: DetailListener) : PagingDataAdapter<Photo, RoverPhotoViewHolder>(RoverPhotoComparator) {

    var currentViewMode = TYPE_LIST

    override fun onBindViewHolder(holder: RoverPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoverPhotoViewHolder {
        return RoverPhotoViewHolder(
            LayoutInflater.from(parent.context),
            parent, listener,
            if (viewType == TYPE_LIST) R.layout.list_item_photo else R.layout.grid_item_photo
        )
    }

    override fun getItemViewType(position: Int): Int {
        return currentViewMode
    }

}
