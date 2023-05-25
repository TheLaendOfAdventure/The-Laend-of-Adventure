package de.hdmstuttgart.thelaendofadventure.logic

import ConversationPopupDialog
import android.content.Context
import android.util.Log
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.ActionRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuestLogic(private val context: Context) {

    companion object {
        private const val TAG = "QuestLogic"
        private const val EXPERIENCE_PER_QUEST = 50
    }

    private val questRepository: QuestRepository = AppDataContainer(context).questRepository
    private val badgeRepository: BadgeRepository = AppDataContainer(context).badgeRepository
    private val actionRepository: ActionRepository = AppDataContainer(context).actionRepository

    /**
     * Updates the quest goal progress for a specific user and checks if the quest is completed.
     * If the quest goal is completed, it also updates the corresponding badge progress for the user.
     *
     * @param questID The ID of the quest.
     * @param goalNumber The new goal number to set.
     * @param userID The ID of the user.
     */
    fun finishedQuestGoal(
        questID: Int,
        goalNumber: Int,
        userID: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                TAG,
                "User userID: $userID completed QuestGoal questID: $questID, questGoal: $goalNumber"
            )

            val updatedGoalNumber = goalNumber + 1

            if (questRepository.updateAndCheckQuestProgressByUserID(
                    userID,
                    questID,
                    updatedGoalNumber
                )
            ) {
                Log.d(TAG, "User userID: $userID completed Quest questID: $questID")
                UserLogic(context).addExperience(userID, EXPERIENCE_PER_QUEST)
                showConversation(userID, questID, updatedGoalNumber)
                updateBadgeProgress(userID, questID)
            }
        }
    }

    private suspend fun updateBadgeProgress(userID: Int, questID: Int) {
        val badgeList = badgeRepository.getBadgesByUserIDAndQuestID(userID, questID).first()

        for (badge in badgeList) {
            val badgeID = badge.badgeID
            val currentGoalNumber = badge.currentGoalNumber

            Log.d(TAG, "Updating badge progress for User userID: $userID, badgeID: $badgeID")

            badgeRepository.updateBadgeProgressByUserID(userID, badgeID, currentGoalNumber + 1)

            Log.d(TAG, "Badge progress updated for User userID: $userID, badgeID: $badgeID")
        }
    }

    private suspend fun showConversation(userID: Int, questID: Int, goalNumber: Int) {
        val dialogPath: String? = actionRepository.getDialogPath(userID, goalNumber, questID)
        if (dialogPath != null) {
            val conversationPopupDialog = ConversationPopupDialog(context, dialogPath)
            conversationPopupDialog.show()
        }
    }
}
