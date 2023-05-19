package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.Manifest
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.thelaendofadventure.data.Tracking
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.helper.MapHelper
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlin.system.exitProcess
import kotlinx.coroutines.launch

class MainPageFragment : Fragment(R.layout.fragment_main_page) {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
    private lateinit var mapHelper: MapHelper
    private lateinit var permissionManager: PermissionManager
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    private val permissionResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            showUserAtMap()
        } else {
            showGpsAlertDialog()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            Tracking(requireContext()).start()
        }
        permissionManager = PermissionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        viewAnnotationManager = binding.mapView.viewAnnotationManager
        mapView = binding.mapView
        val questObserver = Observer<List<QuestEntity>> { questList ->
            mapHelper = MapHelper(mapView, questList, requireContext())
            mapHelper.setUpMap()
        }
        viewModel.quests.observe(viewLifecycleOwner, questObserver)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.userID == -1) {
            Navigation.findNavController(requireView()).navigate(
                R.id.userCreationFragment,
            )
        } else {
            val userObserver = Observer<UserEntity> { user ->
                binding.mainPageProfileLevelDisplay.text = user.level.toString()
                binding.mainPageProfileButton.setImageURI(user.imagePath?.toUri())
            }
            viewModel.user.observe(viewLifecycleOwner, userObserver)

            setUpProfileButton()
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionResultLauncher.launch(permissions)
        }

        val riddleObserver = Observer<List<RiddleDetails>> { riddles ->
            if (riddles.isNotEmpty()) {
                Navigation.findNavController(requireView()).navigate(
                    R.id.navigate_from_main_to_riddle_page
                )
            }
        }
        viewModel.riddleList.observe(viewLifecycleOwner, riddleObserver)
    }

    private fun setUpProfileButton() {
        binding.mainPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_main_to_user_page,
            )
        }
    }

    private fun showUserAtMap() = lifecycleScope.launch {
        // Show user's location at the map
        mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
        }

        // Pass the user's location to camera
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun showGpsAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.gps_required_title)
            .setMessage(R.string.gps_required_context)
            .setPositiveButton(R.string.gps_positiveButton) { dialog, id ->
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionResultLauncher.launch(permissions)
            }
            .setNegativeButton(R.string.gps_negativeButton) { _, _ ->
                exitProcess(0)
            }
        builder.create().show()
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
}
