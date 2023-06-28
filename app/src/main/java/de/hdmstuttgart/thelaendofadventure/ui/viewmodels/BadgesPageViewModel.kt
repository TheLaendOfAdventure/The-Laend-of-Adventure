package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeAction
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeActions
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BadgesPageViewModel(application: Application) : AndroidViewModel(application) {
    private val badgeRepository: BadgeRepository = AppDataContainer(application).badgeRepository
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    val userID = SharedPreferencesHelper.getUserID(application as Context)
    val user = userRepository.getUserByID(userID).asLiveData()

    fun getActionsForBadge(): LiveData<List<BadgeActions>> {
        val actionsLiveData = MutableLiveData<List<BadgeActions>>()

        badgeRepository.getBadgesDetailsByUserID(userID).onEach { badgeList ->
            val actionsList = mutableListOf<BadgeActions>()

            badgeList.forEach { badge ->
                val badgeId = badge.badgeID

                val completedGoalsFlow = badgeRepository.getCompletedGoalsForBadgeByUserID(userID, badgeId)
                val uncompletedGoalsFlow = badgeRepository.getUncompletedGoalsForBadgeByUserID(userID, badgeId)

                val completedGoals = completedGoalsFlow.firstOrNull()?.map { actionEntity ->
                    actionEntity.description
                } ?: emptyList()

                val uncompletedGoals = uncompletedGoalsFlow.firstOrNull()?.map { actionEntity ->
                    actionEntity.description
                } ?: emptyList()

                val completedBadgeAction = BadgeAction(completedGoals, true)
                val uncompletedBadgeAction = BadgeAction(uncompletedGoals, false)

                val badgeActions = BadgeActions(badge, listOf(completedBadgeAction, uncompletedBadgeAction))
                actionsList.add(badgeActions)
            }

            actionsLiveData.postValue(actionsList)
        }.launchIn(viewModelScope)

        return actionsLiveData
    }
}
