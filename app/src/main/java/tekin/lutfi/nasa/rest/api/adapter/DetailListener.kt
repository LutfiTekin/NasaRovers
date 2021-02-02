package tekin.lutfi.nasa.rest.api.adapter

import tekin.lutfi.nasa.rest.api.model.Photo

interface DetailListener {
    fun onSelected(photo: Photo?)
}