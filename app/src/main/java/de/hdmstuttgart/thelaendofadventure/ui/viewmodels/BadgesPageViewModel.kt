package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
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

    fun getActionsForBadge(): LiveData<List<Pair<BadgeDetails, List<Pair<List<String>, Boolean>>>>> {
        val actionsLiveData = MutableLiveData<List<Pair<BadgeDetails, List<Pair<List<String>, Boolean>>>>>()

        badgeRepository.getBadgesDetailsByUserID(userID).onEach { badgeList ->
            val actionsList = mutableListOf<Pair<BadgeDetails, List<Pair<List<String>, Boolean>>>>()

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

                val innerPair = Pair(completedGoals.map { it }, true)
                val innerPair2 = Pair(uncompletedGoals.map { it }, false)

                val actionsPair = Pair(badge, listOf(innerPair, innerPair2))
                actionsList.add(actionsPair)
            }

            actionsLiveData.postValue(actionsList)
        }.launchIn(viewModelScope)

        return actionsLiveData
    }
}
