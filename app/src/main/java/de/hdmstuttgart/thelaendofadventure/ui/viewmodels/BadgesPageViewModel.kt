package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper

class BadgesPageViewModel(application: Application) : AndroidViewModel(application) {
    private val badgeRepository: BadgeRepository = AppDataContainer(application).badgeRepository
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    val userID = SharedPreferencesHelper.getUserID(application as Context)

    val completedBadges = badgeRepository.getCompletedBadgesDetailsByUserID(userID).asLiveData()
    val unCompletedBadges = badgeRepository.getUnCompletedBadgesDetailsByUserID(userID).asLiveData()
    val user = userRepository.getUserByID(userID).asLiveData()
}
