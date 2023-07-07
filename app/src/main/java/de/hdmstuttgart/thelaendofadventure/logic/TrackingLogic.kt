package de.hdmstuttgart.thelaendofadventure.logic

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import de.hdmstuttgart.thelaendofadventure.ui.helper.SnackbarHelper
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.flow.first

/**
 * TrackingLogic class handles location tracking and location-based quest logic for the user.
 *
 * @param context The application context.
 */
@SuppressLint("MissingPermission")
class TrackingLogic(private var context: Context) {

    companion object {
        private const val INTERVAL = 5000L
        private const val ALLOWED_DEVIATION = 0.0001
        private const val TAG = "Tracking"
    }

    /**
     * User's current latitude
     */
    private var latitude: Double = 0.0

    /**
     * User's current longitude
     */
    private var longitude: Double = 0.0

    /**
     *  Repository to handle quest-related data
     */
    private val questRepository: QuestRepository = AppDataContainer(context).questRepository

    /**
     *  User ID obtained from SharedPreferences
     */
    val userID = SharedPreferencesHelper.getUserID(context)

    /**
     * FusedLocationProviderClient for accessing the user's location
     */
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Location request configuration
     */
    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL).apply {
            setWaitForAccurateLocation(true)
        }.build()

    /**
     * Location callback for handling location updates
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // Update user's current location
                latitude = location.latitude
                longitude = location.longitude
            }
        }
    }

    /**
     * Initializer for the TrackingLogic class
     */
    init {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Start the location tracking logic in a coroutine on the IO dispatcher.
     */
    suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val trackableQuest = questRepository.getLocationForAcceptedQuestsByUserID(userID)
            val locationGoals = trackableQuest.first()

            compareLocationGoals(locationGoals)

            delay(INTERVAL)
        }
    }

    /**
     * Compare the user's current location with the location goals of active quests.
     *
     * @param locationGoals The list of [LocationGoal] objects representing quest location goals.
     */
    private suspend fun compareLocationGoals(locationGoals: List<LocationGoal>) {
        locationGoals.forEach { locationGoal ->
            Log.d(TAG, "Checking goal: $locationGoal")
            Log.d(TAG, "Current longitude: $longitude, Current latitude: $latitude")

            val isNearLocation = isLocationNearGoal(locationGoal)

            if (isNearLocation) {
                val questLogic = QuestLogic(context)
                questLogic.finishedQuestGoal(locationGoal.questID, locationGoal.currentGoalNumber)
            }
        }
    }

    /**
     * Check if the user's [latitude] and [longitude] are near the location goal.
     *
     * @param locationGoal The [LocationGoal] to compare with.
     * @return `true` if the user's location is near the goal, `false` otherwise.
     */
    private fun isLocationNearGoal(locationGoal: LocationGoal): Boolean {
        val latitudeDeviation =
            locationGoal.latitude in latitude - ALLOWED_DEVIATION..latitude + ALLOWED_DEVIATION
        val longitudeDeviation =
            locationGoal.longitude in longitude - ALLOWED_DEVIATION..longitude + ALLOWED_DEVIATION

        val isNearLocation = latitudeDeviation && longitudeDeviation
        Log.d(TAG, "Is the current Location near: $isNearLocation")

        return isNearLocation
    }

    /**
     * Check if the user is at the location of the specified quest.
     *
     * @param questID The ID of the quest to check.
     * @param callback The callback function to invoke with the result.
     */
    suspend fun isUserAtQuestLocation(questID: Int, callback: (Boolean) -> Unit) {
        val quest = questRepository.getQuestByQuestID(questID)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatitude = location.latitude
                val currentLongitude = location.longitude

                Log.d(
                    TAG,
                    "Latitude: $currentLatitude and longitude: $currentLongitude"
                )
                Log.d(
                    TAG,
                    "Quest Latitude: ${quest.latitude} and longitude: ${quest.longitude}"
                )

                val isMatchingLocation =
                    (
                        currentLatitude in quest.latitude - ALLOWED_DEVIATION..quest.latitude + ALLOWED_DEVIATION && // ktlint-disable max-line-length
                            currentLongitude in quest.longitude - ALLOWED_DEVIATION..quest.longitude + ALLOWED_DEVIATION // ktlint-disable max-line-length
                        )
                Log.d(
                    TAG,
                    "Is current location matching the target location? $isMatchingLocation"
                )
                callback.invoke(isMatchingLocation)
            } else {
                callback.invoke(false)
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Failed to get location: ${e.message}")
            callback.invoke(false)
        }
    }

    /**
     * Notify the user that they are too far from the specified quest location.
     *
     * @param questID The ID of the quest.
     */
    fun notifyTooFarFromQuest(questID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val quest = questRepository.getQuestByQuestID(questID)
            val imageResID = getImageResourceID(quest.imagePath)
            val text = context.getString(R.string.too_far_away, quest.name)
            showSnackbar(text, imageResID)
        }
    }

    /**
     * Get the resource ID for the specified image path.
     *
     * @param imagePath The path of the image.
     * @return The resource ID of the image.
     */
    @SuppressLint("DiscouragedApi")
    private fun getImageResourceID(imagePath: String?): Int {
        val path = imagePath ?: ""
        return context.resources.getIdentifier(path, "drawable", context.packageName)
    }

    /**
     * Show a Snackbar with the specified message and image resource ID.
     *
     * @param message The message to display in the Snackbar.
     * @param imageResID The resource ID of the image to display.
     */
    private suspend fun showSnackbar(message: String, imageResID: Int) {
        withContext(Dispatchers.Main) {
            val snackbarHelper = SnackbarHelper.getSnackbarInstance()
            snackbarHelper.enqueueSnackbar(context, message, imageResID)
        }
    }
}
