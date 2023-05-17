package de.hdmstuttgart.thelaendofadventure.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@SuppressLint("MissingPermission")
class Tracking(context: Context) {

    companion object {
        private const val INTERVAL = 5000L
        private const val ALLOWED_DEVIATION = 0.0015
    }

    private val tag = "Tracking"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val questRepository: QuestRepository = AppDataContainer(context).questRepository
    val userID = context.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE,
    ).getInt(R.string.userID.toString(), -1)

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
            Looper.getMainLooper(),
        )
    }

    suspend fun start() = CoroutineScope(Dispatchers.IO).launch {
        val trackableQuest = questRepository.getLocationForAcceptedQuestsByUserID(userID)
        trackableQuest.collect { list ->
            CoroutineScope(Dispatchers.Default).launch {
                while (isActive) {
                    Log.d(tag, "Current userID: $userID")
                    Log.d(tag, "Current list: $list")
                    compareLocationGoal(list)
                    delay(INTERVAL)
                }
            }
        }
    }

    private fun compareLocationGoal(locationGoals: List<LocationGoal>) {
        Log.d(tag, "Current longitude: $longitude Current latitude: $latitude")
        locationGoals.forEach { locationGoal ->
            if (locationGoal.latitude in latitude - ALLOWED_DEVIATION..latitude + ALLOWED_DEVIATION && // ktlint-disable max-line-length
                locationGoal.longitude in longitude - ALLOWED_DEVIATION..longitude + ALLOWED_DEVIATION
            ) {
                Log.d(tag, "finished for ${locationGoal.questID}")
                // @todo call followup function for locationGoal.questID locationGoal.questGoalID
            }
        }
    }
}
