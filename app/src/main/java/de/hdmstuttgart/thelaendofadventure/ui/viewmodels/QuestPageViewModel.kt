package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper

class QuestPageViewModel(application: Application) : AndroidViewModel(application) {
    private val questRepository: QuestRepository = AppDataContainer(application).questRepository
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    val userID = SharedPreferencesHelper.getUserID(application as Context)

    val questList = questRepository.getQuestsWithDetailsByUserID(userID).asLiveData()
    val user = userRepository.getUserByID(userID).asLiveData()
}
