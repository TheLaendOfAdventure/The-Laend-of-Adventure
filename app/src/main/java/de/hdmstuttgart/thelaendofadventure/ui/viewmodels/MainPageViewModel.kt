package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Location
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestWithUserLevel
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.MapHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainPageViewModel(private val application: Application) : AndroidViewModel(application) {

    companion object {
        private const val sleepTimer = 100L
    }

    private val userRepository: UserRepository = AppDataContainer(application).userRepository
    private val questRepository: QuestRepository = AppDataContainer(application).questRepository
    private var userID = application.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)
    var location: List<Location> = emptyList()
    val user = userRepository.getUserByID(getUserID()).asLiveData()
    val quests = questRepository.getUnacceptedQuestsByUserID(getUserID())
    val userLevel = userRepository.getLevelByUserID(getUserID())
    val combinedList = combineQuestWithLevel().asLiveData()
    private fun combineQuestWithLevel(): Flow<QuestWithUserLevel> {
        return (
            combine(quests, userLevel) { value1, value2 ->
                QuestWithUserLevel(value1, value2)
            }
            )
    }
    fun getUserID(): Int {
        if (userID == -1) {
            // make sure SharedPreferences is updated
            Thread.sleep(sleepTimer)
            userID = application.getSharedPreferences(
                R.string.sharedPreferences.toString(),
                Context.MODE_PRIVATE
            ).getInt(R.string.userID.toString(), -1)
        }
        return userID
    }
    fun getLocation() {
        CoroutineScope(Dispatchers.IO).launch {
            location = questRepository.getOnlyLocationForAcceptedQuestsByUserID(getUserID())
            location.forEach { location ->
                MapHelper.locationMarkers.add(location)
            }
        }
    }
}
