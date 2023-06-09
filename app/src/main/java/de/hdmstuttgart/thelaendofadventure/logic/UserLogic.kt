package de.hdmstuttgart.thelaendofadventure.logic

import android.content.Context
import android.util.Log
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SnackbarHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserLogic(private val context: Context) {

    companion object {
        private const val TAG = "UserLogic"
        private const val EXPERIENCE_FOR_LEVEL = 100
        private const val DELAY_TIMER: Long = 13000
    }

    private val userRepository: UserRepository = AppDataContainer(context).userRepository

    fun addExperience(userID: Int, experience: Int) {
        Log.d(TAG, "User userID: $userID added $experience experience")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userRepository.getUserByID(userID).first()
                var currentExp = user.exp + experience
                if (currentExp >= EXPERIENCE_FOR_LEVEL) {
                    currentExp -= EXPERIENCE_FOR_LEVEL
                    Log.d(
                        TAG,
                        "User userID: $userID level up new level ${user.level + 1} experience: $currentExp" // ktlint-disable max-line-length
                    )
                    userRepository.updateUserLevel(userID, user.level + 1)
                    notifyLevel(user.level + 1)
                }
                userRepository.updateUserExp(userID, currentExp)
            } catch (e: NoSuchElementException) {
                Log.e(TAG, "ERROR getting user with userID: $userID $e")
            }
        }
    }

    fun increaseWrongAnswerRiddle(userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val newWrongRiddleAnswers: Int = userRepository.getWrongRiddleAnswersByUserID(userID) + 1 // ktlint-disable max-line-length
            userRepository.updateWrongRiddleAnswersByUserID(userID, newWrongRiddleAnswers)
            val badgeGoalEntity = userRepository.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(
                userID
            )
            println("bitte helpen sie mir")
            println(badgeGoalEntity)
            if (badgeGoalEntity != null) {
                // @todo set badgeGoal.isCompleted true
                // notifyBadge(badgeGoalEntity.badgeID)
            }
        }
    }

    private suspend fun notifyLevel(level: Int) {
        delay(DELAY_TIMER)
        val imageResID = R.drawable.chat_icon // @todo implement new Icon
        showSnackbar(context.getString(R.string.level_up_message, level), imageResID)
    }

    private suspend fun showSnackbar(message: String, imageResID: Int) {
        withContext(Dispatchers.Main) {
            SnackbarHelper(context).showTimerSnackbar(message, imageResID)
        }
    }
}
