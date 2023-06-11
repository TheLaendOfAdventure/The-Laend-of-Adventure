package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentMainPageBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestWithUserLevel
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.logic.TrackingLogic
import de.hdmstuttgart.thelaendofadventure.ui.helper.MapHelper
import de.hdmstuttgart.thelaendofadventure.ui.helper.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.helper.Permissions
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.MainPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: MainPageViewModel
    private lateinit var mapView: MapView
    private lateinit var mapHelper: MapHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch { QuestLogic(requireContext()).checkRiddle() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainPageViewModel::class.java]
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        observeQuest()
        return binding.root
    }

    private fun observeQuest() {
        val questObserver = Observer<QuestWithUserLevel> { questList ->
            mapHelper = MapHelper(mapView, questList.quest, requireContext(), questList.userLevel)
            mapHelper.setUpMap()
        }
        viewModel.combinedList.observe(viewLifecycleOwner, questObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn()) {
            requestLocationPermission()
            observeUser()
            setUpProfileButton()
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
        val permissionManager = PermissionManager(requireContext())
        if (permissionManager.checkPermission(Permissions.LOCATION)) {
            showUserAtMap()
        }
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.mainPageProfileLevelDisplay.text = user.level.toString()
            Glide.with(requireContext())
                .load(user.imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.mainPageProfileButton)
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
        lifecycleScope.launch {
            TrackingLogic(requireContext()).start()
        }
        mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
        }
        // pass users location to camera
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
}
