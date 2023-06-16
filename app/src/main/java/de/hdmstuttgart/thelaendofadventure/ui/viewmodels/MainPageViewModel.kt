package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestWithUserLevel
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MainPageViewModel(application: Application) : AndroidViewModel(application) {

    val userID = SharedPreferencesHelper.getUserID(application as Context)

    private val userRepository: UserRepository = AppDataContainer(application).userRepository
    private val questRepository: QuestRepository = AppDataContainer(application).questRepository

    val user = userRepository.getUserByID(userID).asLiveData()
    private val userLevel = userRepository.getLevelByUserID(userID)

    private val quests = questRepository.getUnacceptedQuestsByUserID(userID)

    val combinedList = combineQuestWithLevel().asLiveData()
    private fun combineQuestWithLevel(): Flow<QuestWithUserLevel> {
        return (
            combine(quests, userLevel) { value1, value2 ->
                QuestWithUserLevel(value1, value2)
            }
            )
    }
}
