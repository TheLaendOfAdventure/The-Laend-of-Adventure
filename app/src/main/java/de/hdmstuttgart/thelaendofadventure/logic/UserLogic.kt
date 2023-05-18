package de.hdmstuttgart.thelaendofadventure.logic

import android.content.Context
import android.util.Log
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserLogic(private val context: Context) {

    companion object {
        private const val TAG = "UserLogic"
        private const val EXPERIENCE_FOR_LEVEL = 100
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
                }
                userRepository.updateUserExp(userID, currentExp)
            } catch (e: NoSuchElementException) {
                Log.e(TAG, "ERROR getting user with userID: $userID $e")
            }
        }
    }
}
