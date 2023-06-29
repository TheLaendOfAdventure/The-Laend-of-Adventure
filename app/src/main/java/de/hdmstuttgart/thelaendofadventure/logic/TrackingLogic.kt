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

@SuppressLint("MissingPermission")
class TrackingLogic(private var context: Context) {

    companion object {
        private const val INTERVAL = 5000L
        private const val ALLOWED_DEVIATION = 0.0001
        private const val TAG = "Tracking"
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val questRepository: QuestRepository = AppDataContainer(context).questRepository
    val userID = SharedPreferencesHelper.getUserID(context)

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL).apply {
            setWaitForAccurateLocation(true)
        }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // Update user's current location
                latitude = location.latitude
                longitude = location.longitude
            }
        }
    }

    init {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val trackableQuest = questRepository.getLocationForAcceptedQuestsByUserID(userID)
            val locationGoals = trackableQuest.first()

            compareLocationGoals(locationGoals)

            delay(INTERVAL)
        }
    }

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

    private fun isLocationNearGoal(locationGoal: LocationGoal): Boolean {
        val latitudeDeviation =
            locationGoal.latitude in latitude - ALLOWED_DEVIATION..latitude + ALLOWED_DEVIATION
        val longitudeDeviation =
            locationGoal.longitude in longitude - ALLOWED_DEVIATION..longitude + ALLOWED_DEVIATION

        val isNearLocation = latitudeDeviation && longitudeDeviation
        Log.d(TAG, "Is the current Location near: $isNearLocation")

        return isNearLocation
    }

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

    fun notifyTooFarFromQuest(questID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val quest = questRepository.getQuestByQuestID(questID)
            val imageResID = getImageResourceID(quest.imagePath)
            val text = context.getString(R.string.too_far_away, quest.name)
            showSnackbar(text, imageResID)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getImageResourceID(imagePath: String?): Int {
        val path = imagePath ?: ""
        return context.resources.getIdentifier(path, "drawable", context.packageName)
    }

    private suspend fun showSnackbar(message: String, imageResID: Int) {
        withContext(Dispatchers.Main) {
            val snackbarHelper = SnackbarHelper.getSnackbarInstance()
            snackbarHelper.enqueueSnackbar(context, message, imageResID)
        }
    }
}
