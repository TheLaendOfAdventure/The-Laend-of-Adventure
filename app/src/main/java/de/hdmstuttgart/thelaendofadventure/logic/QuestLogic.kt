package de.hdmstuttgart.thelaendofadventure.logic

import android.content.Context
import android.util.Log
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
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

    /**
     * Updates the quest goal progress for a specific user and checks if the quest is completed.
     * If the quest goal is completed, it also updates the corresponding badge progress for the user.
     *
     * @param questID The ID of the quest.
     * @param goalNumber the new goal number to set
     * @param userID The ID of the user.
     * */
    fun finishedQuestGoal(questID: Int, goalNumber: Int, userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                TAG,
                "User userID: $userID completed QuestGoal questID: $questID, questGoal: $goalNumber"
            )

            if (questRepository.updateAndCheckQuestProgressByUserID(
                    userID,
                    questID,
                    goalNumber + 1
                )
            ) {
                Log.d(TAG, "User userID: $userID completed Quest questID: $questID")
                UserLogic(context).addExperience(userID, EXPERIENCE_PER_QUEST)

                val badgeList = badgeRepository.getBadgesByUserIDAndQuestID(userID, questID).first()
                badgeList.forEach { badges ->
                    val badgeID = badges.badgeID
                    val currentGoalNumber = badges.currentGoalNumber
                    Log.d(
                        TAG,
                        "Updating badge progress for User userID: $userID, badgeID: $badgeID"
                    )
                    badgeRepository.updateBadgeProgressByUserID(
                        userID,
                        badgeID,
                        currentGoalNumber + 1
                    )
                    Log.d(TAG, "Badge progress updated for User userID: $userID, badgeID: $badgeID")
                }
            }
        }
    }
}
