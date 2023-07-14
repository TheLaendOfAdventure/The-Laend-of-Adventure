package de.hdmstuttgart.thelaendofadventure.logic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import de.hdmstuttgart.thelaendofadventure.ui.helper.SnackbarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BadgeLogic(private val context: Context) {

    companion object {
        private const val TAG = "BadgeLogic"
    }

    private val badgeRepository: BadgeRepository = AppDataContainer(context).badgeRepository

    val userID = SharedPreferencesHelper.getUserID(context)

    suspend fun updateBadgeProgress(questID: Int) {
        val userBadgeList = badgeRepository.getUserBadgeGoalsByQuestID(userID, questID).first()
        for (userBadgeEntity in userBadgeList) {
            val badgeID = userBadgeEntity.badgeID

            Log.d(TAG, "Updating badge progress for User userID: $userID, badgeID: $badgeID")

            badgeRepository.completeBadgeGoalByUserID(userID, badgeID, userBadgeEntity.badgeGoalID)
            notifyBadge(badgeID)

            Log.d(TAG, "Badge progress updated for User userID: $userID, badgeID: $badgeID")
        }
    }

    suspend fun checkWrongRiddleAnswersBadge() {
        val badgeGoalEntity = badgeRepository.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(
            userID
        )
        if (badgeGoalEntity != null) {
            badgeRepository.completeBadgeGoalByUserID(userID, badgeGoalEntity.badgeID, badgeGoalEntity.badgeGoalID)
            notifyBadge(badgeGoalEntity.badgeID)
        }
    }

    private suspend fun notifyBadge(badgeID: Int) {
        val badge = badgeRepository.getBadgeByBadgeID(badgeID)
        val imageResID = getImageResourceID(badge!!.imagePath)
        showSnackbar(context.getString(R.string.badge_completed_message, badge.name), imageResID)
    }

    @SuppressLint("DiscouragedApi")
    private fun getImageResourceID(imagePath: String?): Int {
        val path = imagePath ?: ""
        return context.resources.getIdentifier(path, "drawable", context.packageName)
    }

    private suspend fun showSnackbar(message: String, imageResID: Int) {
        withContext(Dispatchers.Main) {
            val snackbarHelper = SnackbarHelper.getSnackbarInstance()
            snackbarHelper.enqueueSnackbar(context, message, imageResID)
        }
    }
}
