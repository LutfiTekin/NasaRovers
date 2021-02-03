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
            val requestedPage = params.key ?: 0
            var data = source.loadPhotos(requestedPage)

            //Check if current sol is consumed
            if (data.size < PAGE_SIZE && source.selectedSol > 1){
                //reset page to default
                source.page = 1
                //request a new sol data from data source
                val dataFromNewSol = source.loadPhotos(solConsumed = true)
                data = data.toMutableList().apply {
                    addAll(dataFromNewSol) //load first list data from the new sol
                }.toList()
            }

            "Loaded page size ${data.size}".toConsole(true)

            LoadResult.Page(
                data,
                prevKey = if (requestedPage == 0) null else requestedPage - 1,
                nextKey = if (data.size < PAGE_SIZE) null else source.page + 1
            )
        } catch (e: Exception) {
            "${e.message}".toConsole()
            LoadResult.Error(e)
        }
    }


}