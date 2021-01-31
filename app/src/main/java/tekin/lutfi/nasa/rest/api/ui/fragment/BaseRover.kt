package tekin.lutfi.nasa.rest.api.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.adapter.PhotosAdapter
import tekin.lutfi.nasa.rest.api.adapter.PhotoLoadingStateAdapter
import tekin.lutfi.nasa.rest.api.defaultRetrofit
import tekin.lutfi.nasa.rest.api.paging.ListPagingSource
import tekin.lutfi.nasa.rest.api.paging.PAGE_SIZE
import tekin.lutfi.nasa.rest.api.service.PhotosApi
import tekin.lutfi.nasa.rest.api.ui.viewmodel.RoverViewModel


const val CURIOSITY = "curiosity"
const val OPPORTUNITY = "opportunity"
const val SPIRIT = "spirit"

open class BaseRover: Fragment() {

    private val viewModel: RoverViewModel by activityViewModels()
    private lateinit var attachedContext: Context
    open val roverName: String = ""
    open val availableCameras = arrayOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rover, container, false)
    }

    private val photosAdapter = PhotosAdapter()

    private val photosRV by lazy {
        view?.findViewById<RecyclerView>(R.id.photosRV)
    }

    private val retrofit by lazy {
        attachedContext.defaultRetrofit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photosRV?.apply {
            setHasFixedSize(true)
            adapter = photosAdapter.withLoadStateFooter(
                footer = PhotoLoadingStateAdapter{photosAdapter.retry()}//TODO FIX
            )
        }
        val paged = Pager(PagingConfig(PAGE_SIZE)){
            val source = PhotosApi(roverName, retrofit, null)
            ListPagingSource(source)
        }.flow.cachedIn(lifecycleScope)
        lifecycleScope.launch {
            paged.collectLatest {
                photosAdapter.submitData(it)
            }
        }

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachedContext = context
    }

}