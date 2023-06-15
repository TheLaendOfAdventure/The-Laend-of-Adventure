package de.hdmstuttgart.thelaendofadventure.logic

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
                compareLocationGoal(list)
                delay(INTERVAL)
            }
        }
    }

    private fun compareLocationGoal(locationGoals: List<LocationGoal>) {
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
                questLogic.finishedQuestGoal(
                    locationGoal.questID,
                    locationGoal.currentGoalNumber
                )
                return
            }
        }
    }
}
