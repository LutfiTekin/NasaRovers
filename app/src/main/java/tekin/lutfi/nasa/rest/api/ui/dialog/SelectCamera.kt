package tekin.lutfi.nasa.rest.api.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.ui.fragment.AVAILABLE_CAMERAS
import tekin.lutfi.nasa.rest.api.ui.fragment.SELECTED_ROVER
import tekin.lutfi.nasa.rest.api.viewmodel.RoverViewModel

class SelectCamera: BottomSheetDialogFragment() {

    private val roverViewModel: RoverViewModel by activityViewModels()
    private lateinit var attachedContext: Context
    private var selectedCamera = ""
    private var selectedRover = ""
    private var availableCameras = arrayOf("")

    private val selectCameraButton by lazy {
        view?.findViewById<Button>(R.id.selectCameras)
    }

    private val camChipGroup: ChipGroup? by lazy {
        view?.findViewById(R.id.camChips)
    }

    private val camInfo: TextView? by lazy {
        view?.findViewById(R.id.camInfo)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.getStringArray(AVAILABLE_CAMERAS)?.let {
            availableCameras = it
        }
        selectedRover = arguments?.getString(SELECTED_ROVER).orEmpty()
        selectedCamera = roverViewModel.selectedCamera.value?.get(selectedRover).orEmpty()

        return inflater.inflate(R.layout.dialog_select_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCamInfo(selectedCamera)
        setupChipGroup()
        selectCameraButton?.setOnClickListener {
            val map = roverViewModel.selectedCamera.value?.toMutableMap() ?: mutableMapOf()
            map[selectedRover] = selectedCamera
            roverViewModel.selectedCamera.value = map.toMap()
            logCamSelection()
            dismissAllowingStateLoss()
        }

    }

    private fun logCamSelection(){
        Firebase.analytics.logEvent("camera-selected") {
            param("rover", selectedRover)
            param("camera", selectedCamera.ifBlank { "all" })
        }
    }

    private fun setupChipGroup() {
        camChipGroup?.removeAllViews()
        availableCameras.forEach { camera ->
            //Create chip view for each camare
            val chip = Chip(attachedContext).apply {
                text = camera
                isCheckable = true
                isChecked = camera == selectedCamera
                setOnCheckedChangeListener { buttonView, isChecked ->
                    //Check if this is a user interaction
                    if (buttonView.isPressed.not())
                        return@setOnCheckedChangeListener
                    /**
                     * Manually clearing all selected chips because
                     * app:singleSelection is not working as it should
                     */
                    camChipGroup?.clearCheck()
                    selectedCamera = if (isChecked) {
                        setCamInfo(buttonView.text.toString())
                    } else {
                        camInfo?.text = getString(R.string.cam_info)
                        ""
                    }

                    buttonView.isChecked = isChecked
                }
            }
            camChipGroup?.addView(chip)
        }
    }

    private fun setCamInfo(camera: String): String{
        if (camera.isNotBlank()) {
            val resId = resources.getIdentifier(
                "cam_$camera",
                "string",
                attachedContext.packageName
            )
            camInfo?.text = attachedContext.getString(resId)
        }else {
            camInfo?.text = getString(R.string.cam_info)
        }
        return camera
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachedContext = context
    }
}