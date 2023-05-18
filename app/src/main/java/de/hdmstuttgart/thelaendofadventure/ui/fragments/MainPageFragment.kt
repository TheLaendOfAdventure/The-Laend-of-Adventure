package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.Manifest // ktlint-disable import-ordering
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.thelaendofadventure.data.Tracking
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
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
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        mapView.getMapboxMap().loadStyleUri(getString(R.string.mapbox_styleURL))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]

        if (viewModel.userID == -1) {
            Navigation.findNavController(requireView()).navigate(
                R.id.userCreationFragment
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
                R.id.navigate_from_main_to_user_page
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

    private fun showRiddlePopUp(
        actionID: Number
    ) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.riddle_popup, null)

        val answer1Button = dialogView.findViewById<Button>(R.id.answer_option_1)
        answer1Button.text
        answer1Button.setOnClickListener{}

        val answer2Button = dialogView.findViewById<Button>(R.id.answer_option_2)
        answer2Button.text
        answer2Button.setOnClickListener{}

        val answer3Button = dialogView.findViewById<Button>(R.id.answer_option_3)
        answer3Button.text
        answer3Button.setOnClickListener{}

        val answer4Button = dialogView.findViewById<Button>(R.id.answer_option_4)
        answer4Button.text
        answer4Button.setOnClickListener{}

        var buidler = AlertDialog.Builder(requireContext())
        buidler.setView(dialogView)

        buidler.create()
        buidler.show()
    }
}
