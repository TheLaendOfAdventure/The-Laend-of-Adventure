package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.the_laend_of_adventure.databinding.PopupDialogBinding
import de.hdmstuttgart.thelaendofadventure.BitmapUtils
import de.hdmstuttgart.thelaendofadventure.data.Tracking
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlin.system.exitProcess
import kotlinx.coroutines.launch

class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
    private lateinit var permissionManager: PermissionManager
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var pointAnnotation: PointAnnotation
    private lateinit var viewAnnotation: View
    private lateinit var iconBitmap: Bitmap
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        iconBitmap = BitmapUtils.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.chat_icon,
        )!!
        lifecycleScope.launch {
            Tracking(requireContext()).start()
        }

        permissionManager = PermissionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        viewAnnotationManager = binding.mapView.viewAnnotationManager

        mapView = binding.mapView
        setUpMap()
        return binding.root
    }

    private fun setUpMap() {
        binding.mapView.getMapboxMap().loadStyleUri(getString(R.string.mapbox_styleURL)) {
            prepareAnnotationMarker(binding.mapView, iconBitmap)
            prepareViewAnnotation()
            // show / hide view annotation based on a marker click
            pointAnnotationManager.addClickListener { clickedAnnotation ->
                if (pointAnnotation == clickedAnnotation) {
                    viewAnnotation.toggleViewVisibility()
                }
                true
            }
        }
    }
    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    private fun prepareAnnotationMarker(mapView: MapView, iconBitmap: Bitmap) {
        val annotationPlugin = mapView.annotations
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(POINT)
            .withIconImage(iconBitmap)
            .withIconAnchor(IconAnchor.BOTTOM)
            .withDraggable(false)
        pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
    }

    @SuppressLint("SetTextI18n")
    private fun prepareViewAnnotation() {
        viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.popup_dialog,
            options = viewAnnotationOptions {
                geometry(POINT)
                associatedFeatureId(pointAnnotation.featureIdentifier)
                anchor(ViewAnnotationAnchor.BOTTOM)
                offsetY((pointAnnotation.iconImageBitmap?.height!!).toInt())
            },
        )

        viewAnnotation.visibility = View.GONE

        PopupDialogBinding.bind(viewAnnotation).apply {
            popupDialogDeclineButton.setOnClickListener {
                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
            }
            popupDialogAcceptButton.setOnClickListener {
                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]

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

    private companion object {
        val POINT: Point = Point.fromLngLat(9.179736659033168, 48.778604067433584)
    }
}
