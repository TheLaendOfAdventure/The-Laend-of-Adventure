package de.hdmstuttgart.thelaendofadventure

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.ActivityFullscreenBinding
import de.hdmstuttgart.thelaendofadventure.data.Tracking
import de.hdmstuttgart.thelaendofadventure.ui.fragments.UserCreationFragment
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private val tag = "FullscreenActivity"
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)
    private lateinit var mapView: MapView
    private var gameStarted = false
    val userID = this.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE,
    ).getInt(R.string.userID.toString(), -1)

    private var isFullscreen: Boolean = false

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= ANDROID11) {
            WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private val hideRunnable = Runnable {
        hide()
    }

    // Get the user's location as coordinates
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }

            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfRequiredPermissionIsGranted()

        setContentView(R.layout.activity_fullscreen)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)

        mapView = findViewById(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(getString(R.string.mapbox_styleURL))

        if (userID == -1) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<UserCreationFragment>(R.id.activity_fullscreen)
                addToBackStack(null)
            }
        } else {
            setupGame()
        }
    }

    fun setupGame() {
        if (!gameStarted) {
            Log.d(tag, "Starting necessary game functions")
            showUserAtMap()
            lifecycleScope.launch {
                Tracking(this@FullscreenActivity).start()
            }
            gameStarted = true
        }
    }

    private fun showUserAtMap() {
        // Show user's location at the map
        mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
        }

        // Pass the user's location to camera
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun checkIfRequiredPermissionIsGranted() {
        when {
            ContextCompat.checkSelfPermission(
                this@FullscreenActivity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this@FullscreenActivity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) -> {
                showGpsAlertDialog()
            }

            else -> {
                // Ask for both the ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions.
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                    requestCodeLocation,
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodeLocation -> {
                // If the request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // ACCESS_FINE_LOCATION is granted
                    } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // ACCESS_COARSE_LOCATION is granted
                    } else {
                        showGpsAlertDialog()
                    }
                }
                return
            }
        }
    }

    private fun showGpsAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.gps_required_title))
            .setMessage(R.string.gps_required_context)
            .setPositiveButton(R.string.gps_positiveButton) { dialog, id ->
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                    requestCodeLocation,
                )
            }
            .setNegativeButton(R.string.gps_negativeButton) { dialog, id ->
                exitProcess(0)
            }
        builder.create().show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(DELAY_TIME_MS)
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
        private const val ANDROID11 = 30
        private const val DELAY_TIME_MS = 100
        private const val requestCodeLocation = 100
    }
}
