package de.hdmstuttgart.thelaendofadventure.logic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.ActionRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.dialogpopup.RiddlePopupDialog
import de.hdmstuttgart.thelaendofadventure.ui.helper.SnackbarHelper
import de.hdmstuttgart.thelaendofadventure.ui.popupwindow.ConversationPopupDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestLogic(private val context: Context) {

    companion object {
        private const val TAG = "QuestLogic"
        private const val EXPERIENCE_PER_QUEST = 50
    }

    private val questRepository: QuestRepository = AppDataContainer(context).questRepository
    private val badgeRepository: BadgeRepository = AppDataContainer(context).badgeRepository
    private val actionRepository: ActionRepository = AppDataContainer(context).actionRepository

    val userID = context.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)

    /**
     * Updates the quest goal progress for a specific user and checks if the quest is completed.
     * If the quest goal is completed, it also updates the corresponding badge progress for the user.
     *
     * @param questID The ID of the quest.
     * @param goalNumber The new goal number to set.
     */
    fun finishedQuestGoal(
        questID: Int,
        goalNumber: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                TAG,
                "User userID: $userID completed QuestGoal questID: $questID, questGoal: $goalNumber"
            )

            if (goalNumber == 0) {
                questRepository.assignQuestToUser(userID, questID)
            }

            val updatedGoalNumber = goalNumber + 1

            if (questRepository.updateAndCheckQuestProgressByUserID(
                    userID,
                    questID,
                    updatedGoalNumber
                )
            ) {
                Log.d(TAG, "User userID: $userID completed Quest questID: $questID")
                notifyQuest(questID)

                UserLogic(context).addExperience(userID, EXPERIENCE_PER_QUEST)
                updateBadgeProgress(questID)
            } else {
                notifyGoal(questID, goalNumber)
            }
            showConversation(questID, updatedGoalNumber)
        }
    }

    private suspend fun notifyGoal(questID: Int, goalNumber: Int) {
        val name: String
        val message: String
        if (goalNumber == 0) {
            val quest = questRepository.getQuestByQuestID(questID)
            name = quest.name
            message = context.getString(R.string.quest_accept_message, name)
        } else {
            name = questRepository.getNameByQuestByGoal(questID, goalNumber)
            message = context.getString(R.string.goal_completed_message, name)
        }
        val imageResID = getImageResourceID("")
        showSnackbar(message, imageResID)
    }

    private suspend fun notifyQuest(questID: Int) {
        val quest = questRepository.getQuestByQuestID(questID)
        val imageResID = getImageResourceID(quest.imagePath)
        showSnackbar(context.getString(R.string.quest_completed_message, quest.name), imageResID)
    }

    @SuppressLint("DiscouragedApi")
    private fun getImageResourceID(imagePath: String?): Int {
        val path = imagePath ?: ""
        return context.resources.getIdentifier(path, "drawable", context.packageName)
    }

    private suspend fun showSnackbar(message: String, imageResID: Int) {
        withContext(Dispatchers.Main) {
            SnackbarHelper(context).showTimerSnackbar(message, imageResID)
        }
    }

    private suspend fun showConversation(questID: Int, goalNumber: Int) {
        val dialogPath: String? = if (goalNumber == 1) {
            questRepository.getDialogPathByQuestID(questID)
        } else {
            actionRepository.getDialogPath(userID, goalNumber, questID)
        }

        Log.d(TAG, "dialogPath: $dialogPath")

        if (dialogPath != null) {
            val questImage = questRepository.getQuestImageByQuestID(questID)
            withContext(Dispatchers.Main) {
                val conversationPopupDialog = ConversationPopupDialog(
                    context,
                    dialogPath,
                    userID,
                    questImage ?: "no path needed" // is needed for null pointer exception
                )
                conversationPopupDialog.show()
                conversationPopupDialog.setOnDismissListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        checkRiddle()
                        Log.d(TAG, "Launch RiddlePopup!")
                    }
                }
            }
        } else {
            checkRiddle()
        }
    }

    suspend fun checkRiddle() {
        val riddleList = questRepository.getRiddleForAcceptedQuestsByUserID(userID).first()
        Log.d(TAG, "RiddleList: $riddleList")
        if (riddleList.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                val riddlePopupDialog = RiddlePopupDialog(context, riddleList)
                riddlePopupDialog.show()
                Log.d(TAG, "Riddle should be shown!")
                riddlePopupDialog.setOnDismissListener {
                    Log.d(TAG, "Riddle has been closed!")
                }
            }
        }
    }

    private suspend fun updateBadgeProgress(questID: Int) {
        val badgeList = badgeRepository.getBadgesByUserIDAndQuestID(userID, questID).first()

        for (badge in badgeList) {
            val badgeID = badge.badgeID
            val currentGoalNumber = badge.currentGoalNumber

            Log.d(TAG, "Updating badge progress for User userID: $userID, badgeID: $badgeID")

            badgeRepository.updateBadgeProgressByUserID(userID, badgeID, currentGoalNumber + 1)
            notifyBadge(badgeID)

            Log.d(TAG, "Badge progress updated for User userID: $userID, badgeID: $badgeID")
        }
    }

    private suspend fun notifyBadge(badgeID: Int) {
        val badge = badgeRepository.getBadgesByBadgeID(badgeID)
        val imageResID = getImageResourceID(badge.imagePath)
        showSnackbar(context.getString(R.string.goal_completed_message, badge.name), imageResID)
    }
}
