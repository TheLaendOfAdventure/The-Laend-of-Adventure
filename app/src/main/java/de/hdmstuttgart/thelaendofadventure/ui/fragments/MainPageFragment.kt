package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.permissions.Permissions
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlinx.coroutines.launch

class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        mapView.getMapboxMap().loadStyleUri(getString(R.string.mapbox_styleURL))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]

        val userObserver = Observer<UserEntity> { user ->
            binding.mainPageProfileLevelDisplay.text = user.level.toString()
            binding.mainPageProfileButton.setImageURI(user.imagePath?.toUri())
        }
        viewModel.user.observe(viewLifecycleOwner, userObserver)

        setUpProfileButton()
        if (permissionManager.checkPermission(Permissions.ACCESS_LOCATION)) {
            showUserAtMap()
        }
    }

    private fun setUpProfileButton() {
        binding.mainPageProfileButton.setOnClickListener {
            // @todo Navigation here
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

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
}
