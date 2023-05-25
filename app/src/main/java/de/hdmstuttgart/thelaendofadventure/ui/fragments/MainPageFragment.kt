package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.Manifest // ktlint-disable import-ordering
import android.app.AlertDialog
import android.os.Bundle
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
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.thelaendofadventure.data.Tracking
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.helper.MapHelper
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private var isPopupShown = false
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
    private lateinit var mapHelper: MapHelper
    private lateinit var permissionManager: PermissionManager

    private val permissionResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            showUserAtMap()
        } else {
            showGpsAlertDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
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
        if (isUserLoggedIn()) {
            requestLocationPermission()
            observeUser()
            setUpProfileButton()
            observeRiddles()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val userID = viewModel.getUserID()
        if (userID == -1) {
            Navigation.findNavController(requireView()).navigate(
                R.id.userCreationFragment
            )
            return false
        }
        return true
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionResultLauncher.launch(permissions)
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.mainPageProfileLevelDisplay.text = user.level.toString()
            binding.mainPageProfileButton.setImageURI(user.imagePath?.toUri())
        }
    }

    private fun setUpProfileButton() {
        binding.mainPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_main_to_user_page
            )
        }
    }

    private fun observeRiddles() {
        viewModel.riddleList.observe(viewLifecycleOwner) { riddles ->
            if (riddles.isNotEmpty() && !isPopupShown) {
                isPopupShown = true
                val riddlePopupFragment = RiddlePopupFragment(riddles)
                riddlePopupFragment.setOnDismissListener {
                    isPopupShown = false
                }
                riddlePopupFragment.show(parentFragmentManager, "RiddlePopup")
            }
        }
    }

    private fun showUserAtMap() = lifecycleScope.launch {
        permissionManager = PermissionManager(requireContext())
        lifecycleScope.launch {
            Tracking(requireContext()).start()
        }
        mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
        }
        passUserLocationToCamera()
    }

    private fun passUserLocationToCamera() {
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())

    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private fun showGpsAlertDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.gps_required_title)
            setMessage(R.string.gps_required_context)
            setPositiveButton(R.string.gps_positiveButton) { dialog, _ ->
                requestLocationPermission()
                dialog.dismiss()
            }
            setNegativeButton(R.string.gps_negativeButton) { _, _ ->
                exitProcess(0)
            }
            create().show()
        }
    }
}
