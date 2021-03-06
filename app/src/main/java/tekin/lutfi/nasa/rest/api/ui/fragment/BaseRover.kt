package tekin.lutfi.nasa.rest.api.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.adapter.*
import tekin.lutfi.nasa.rest.api.rest.model.Photo
import tekin.lutfi.nasa.rest.api.paging.ListPagingSource
import tekin.lutfi.nasa.rest.api.paging.PAGE_SIZE
import tekin.lutfi.nasa.rest.api.rest.data.PhotosApi
import tekin.lutfi.nasa.rest.api.ui.dialog.ROVER_PHOTO
import tekin.lutfi.nasa.rest.api.viewmodel.MODE_GRID
import tekin.lutfi.nasa.rest.api.viewmodel.MODE_LIST
import tekin.lutfi.nasa.rest.api.viewmodel.RoverViewModel


const val CURIOSITY = "curiosity"
const val OPPORTUNITY = "opportunity"
const val SPIRIT = "spirit"
const val PERSEVERANCE = "perseverance"

const val AVAILABLE_CAMERAS = "ac"
const val SELECTED_ROVER = "sr"

abstract class BaseRover : Fragment(), DetailListener, PhotosApi.Callback {

    private val viewModel: RoverViewModel by activityViewModels()
    private lateinit var attachedContext: Context
    abstract val roverName: String
    abstract val availableCameras: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "rover")
            param(FirebaseAnalytics.Param.ITEM_NAME, roverName)
        }
        return inflater.inflate(R.layout.fragment_rover, container, false)
    }

    private val photosAdapter = PhotosAdapter(this)

    private var photosRV : RecyclerView? = null


    private val concatAdapter by lazy {
        photosAdapter.withLoadStateFooter(
            footer = PhotoLoadingStateAdapter { photosAdapter.retry() }
        )
    }

    private val loadingToast: Toast by lazy {
        Toast.makeText(attachedContext,getString(R.string.toast_loading_images),Toast.LENGTH_LONG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupUI()
        loadRoverPhotos()
        loadingToast.show()
        viewModel.selectedCamera.observe(viewLifecycleOwner, { map ->
            loadingToast.show()
            val selectedCamera = map[roverName]
            if (selectedCamera.isNullOrBlank())
                loadRoverPhotos()
            else
                loadRoverPhotos(selectedCamera)
        })
    }

    private fun loadRoverPhotos(camera: String? = null) {
        val source = PhotosApi(roverName, attachedContext, camera, this)
        val paged = Pager(PagingConfig(PAGE_SIZE)) {
            ListPagingSource(source)
        }.flow.cachedIn(lifecycleScope)
        lifecycleScope.launch {
            paged.collectLatest {
                photosAdapter.submitData(it)
            }
        }

    }

    private fun View.setupUI() {
        photosRV = findViewById(R.id.photosRV)
        photosRV?.apply {
            setHasFixedSize(true)
            adapter = concatAdapter
        }

        viewModel.recyclerViewMode.observe(viewLifecycleOwner, { mode ->
            val selectedMode = mode ?: MODE_LIST
            val newLayoutManager = if (selectedMode == MODE_GRID) GridLayoutManager(
                attachedContext,
                2
            ) else LinearLayoutManager(attachedContext)
            if (photosRV?.layoutManager?.javaClass != newLayoutManager.javaClass){
                photosRV?.layoutManager = newLayoutManager
            }
            photosAdapter.currentViewMode = if (selectedMode == MODE_GRID) TYPE_GRID else TYPE_LIST
            photosAdapter.notifyItemRangeChanged(0,10)
        })

        viewModel.launchFilterDialog.observe(viewLifecycleOwner, { shouldLaunch ->
            if (shouldLaunch == null)
                return@observe
            findNavController().navigate(R.id.selectCamera, Bundle().apply {
                putStringArray(AVAILABLE_CAMERAS,availableCameras)
                putString(SELECTED_ROVER,roverName)
            })
            //Action is consumed, data should be cleared
            viewModel.launchFilterDialog.value = null

        })
    }

    override fun onFirstPageLoaded() {
        if (isAdded) //if the current fragment is added to stack
            loadingToast.cancel()
    }

    override fun onSelected(photo: Photo?) {
        findNavController().navigate(R.id.photoDetail, Bundle().apply {
            putString(ROVER_PHOTO,Gson().toJson(photo))
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachedContext = context
    }

}