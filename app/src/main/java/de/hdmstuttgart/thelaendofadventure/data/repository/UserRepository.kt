package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for interacting with the User table in the database.
 */
interface UserRepository {

    /**
     * Add a user to the database.
     *
     * @param user the [UserEntity] to add to the database.
     */
    fun addUser(user: UserEntity): Long

    /**
     * Get a specific user from the database.
     *
     * @param userID the ID of the user to retrieve.
     * @return a flow of the [UserEntity] with the given ID.
     */
    fun getUserByID(userID: Int): Flow<UserEntity>

    /**
     * Get the level of a specific user in the database.
     *
     * @param userID the ID of the user.
     * @return the level as Flow<Int>.
     */
    fun getLevelByUserID(userID: Int): Flow<Int>

    /**
     * Update the name of a specific user in the database.
     *
     * @param userID the ID of the user to update.
     * @param name the new name for the user.
     */
    suspend fun updateUserName(userID: Int, name: String)

    /**
     * Update the image path of a specific user in the database.
     *
     * @param userID the ID of the user to update.
     * @param imagePath the new image path for the user.
     */
    suspend fun updateUserImagePath(userID: Int, imagePath: String)

    /**
     * Update the level of a specific user in the database.
     *
     * @param userID the ID of the user to update.
     * @param level the new level for the user.
     */
    suspend fun updateUserLevel(userID: Int, level: Int)

    /**
     * Update the experience points of a specific user in the database.
     *
     * @param userID the ID of the user to update.
     * @param exp the new experience points for the user.
     */
    suspend fun updateUserExp(userID: Int, exp: Int)

    /**
     * Get the wrong Riddle-answers of a specific user in the database.
     *
     * @param userID the ID of the user.
     * @return the wrong Riddle-answers as [Int].
     */
    suspend fun getWrongRiddleAnswersByUserID(userID: Int): Int

    /**
     * increase the WrongRiddleAnswers field in the database by 1.
     *
     * @param userID the ID of the user to update.
     * @param wrongRiddleAnswers the new count for wrong Riddle-answers for the user.
     */
    suspend fun updateWrongRiddleAnswersByUserID(userID: Int, wrongRiddleAnswers: Int)
}
