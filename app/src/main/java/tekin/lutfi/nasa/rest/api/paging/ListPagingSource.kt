package tekin.lutfi.nasa.rest.api.paging

import androidx.paging.PagingSource
import tekin.lutfi.nasa.rest.api.model.Photo
import tekin.lutfi.nasa.rest.api.service.PhotosApi
import tekin.lutfi.nasa.rest.api.toConsole

const val PAGE_SIZE = 25

class ListPagingSource(private val source: PhotosApi) :
    PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {

        return try {
            val nextPage = params.key ?: 0
            val data = source.loadPhotos(nextPage)

            LoadResult.Page(
                data,
                if (nextPage == 0) null else nextPage - 1,
                if (data.size < PAGE_SIZE) null else source.page + 1
            )
        } catch (e: Exception) {
            "${e.message}".toConsole()
            LoadResult.Error(e)
        }
    }


}