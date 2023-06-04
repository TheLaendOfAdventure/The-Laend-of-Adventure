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
     * Get all users from the database.
     *
     * @return emitting a list of [UserEntity] in the database.
     */
    fun getAllUsers(): Flow<List<UserEntity>>

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
     * @param userID the ID of the user to update.
     * @return the level as [Int].
     */
    fun getLevelByUserID(userID: Int): Int

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
     * Update the walked kilometers of a specific user in the database.
     *
     * @param userID the ID of the user to update.
     * @param walkedKm the new walked kilometers for the user.
     */
    suspend fun updateUserWalkedKm(userID: Int, walkedKm: Int)
}
