package de.hdmstuttgart.thelaendofadventure.logic

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.* // ktlint-disable no-wildcard-imports
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.flow.collectLatest

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
        val trackableQuest = questRepository.getLocationForAcceptedQuestsByUserID(userID)
        trackableQuest.collectLatest { list ->
            while (isActive) {
                Log.d(TAG, "Current userID: $userID")
                Log.d(TAG, "Current list: $list")
                withContext(Dispatchers.IO) {
                    compareLocationGoal(list)
                }
                delay(INTERVAL)
            }
        }
    }

    private suspend fun compareLocationGoal(locationGoals: List<LocationGoal>) {
        Log.d(TAG, "Current longitude: $longitude Current latitude: $latitude")
        locationGoals.forEach { locationGoal ->
            if (locationGoal.latitude in latitude - ALLOWED_DEVIATION..latitude + ALLOWED_DEVIATION && // ktlint-disable max-line-length
                locationGoal.longitude in longitude - ALLOWED_DEVIATION..longitude + ALLOWED_DEVIATION
            ) {
                Log.d(
                    TAG,
                    "finished Goal ${locationGoal.currentGoalNumber} for questID: ${locationGoal.questID}"
                )
                val questLogic = QuestLogic(context)
                Log.d("QuestLogic", "call finish goal in Tracking ")
                withContext(Dispatchers.IO) {
                    questLogic.finishedQuestGoal(
                        locationGoal.questID,
                        locationGoal.currentGoalNumber
                    )
                }
            }
        }
    }

    suspend fun isUserAtQuestLocation(questID: Int, callback: (Boolean) -> Unit) {
        val quest = questRepository.getQuestByQuestID(questID)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
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
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Failed to get location: ${e.message}")
                callback.invoke(false)
            }
    }
}
