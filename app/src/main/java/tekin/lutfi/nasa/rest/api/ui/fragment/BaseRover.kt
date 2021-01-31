package tekin.lutfi.nasa.rest.api.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.ui.viewmodel.RoverViewModel

open class BaseRover: Fragment() {

    private val viewModel: RoverViewModel by activityViewModels()
    private lateinit var attachedContext: Context
    open val name: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Load first page at start
        loadPhotos()
    }

    private fun loadPhotos(page: Int = 1) {
        lifecycleScope.launch {
            Log.d("TEST","loading")
            viewModel.getRoverPhotos(attachedContext, name, page)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachedContext = context
    }

}