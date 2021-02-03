package tekin.lutfi.nasa.rest.api.paging

import androidx.recyclerview.widget.DiffUtil
import tekin.lutfi.nasa.rest.api.rest.model.Photo

object RoverPhotoComparator: DiffUtil.ItemCallback<Photo>() {
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.imgSrc == newItem.imgSrc
    }

    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return try {
            oldItem == newItem
        } catch (e: Exception) {
            false
        }
    }
}