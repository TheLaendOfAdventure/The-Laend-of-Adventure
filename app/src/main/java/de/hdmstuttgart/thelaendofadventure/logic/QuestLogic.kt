package de.hdmstuttgart.thelaendofadventure.logic

import android.content.Context
import android.util.Log
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
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

    fun finishedQuestGoal(questID: Int, goalNumber: Int, userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            questRepository.updateQuestProgressByUserID(
                userID,
                questID,
                goalNumber + 1
            )
            Log.d(
                TAG,
                "User userID: $userID completed QuestGoal questID: $questID, questGoal: $goalNumber"
            )

            val progress = questRepository.getProgressForQuestByUserID(userID, questID).first()
            if (progress.currentGoalNumber == progress.targetGoalNumber) {
                Log.d(TAG, "User userID: $userID completed Quest questID: $questID")
                UserLogic(context).addExperience(userID, EXPERIENCE_PER_QUEST)
            }
        }
    }
}
