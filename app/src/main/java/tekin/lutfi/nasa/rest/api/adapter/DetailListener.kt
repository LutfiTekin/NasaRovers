package tekin.lutfi.nasa.rest.api.adapter

import tekin.lutfi.nasa.rest.api.rest.model.Photo

interface DetailListener {
    fun onSelected(photo: Photo?)
}