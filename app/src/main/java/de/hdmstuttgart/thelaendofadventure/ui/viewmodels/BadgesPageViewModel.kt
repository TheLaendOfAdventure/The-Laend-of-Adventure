package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository

class BadgesPageViewModel(application: Application) : AndroidViewModel(application) {
    private val badgeRepository: BadgeRepository = AppDataContainer(application).badgeRepository

    val userID = application.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)

    val badges = badgeRepository.getAcceptedBadgesByUserID(userID).asLiveData()

    /*val badges: LiveData<List<BadgeEntity>> = MutableLiveData()

    init {
        fetchBadges()
    }

    private fun fetchBadges() {
        badgeRepository.getAcceptedBadgesByUserID()
    }*/
}
