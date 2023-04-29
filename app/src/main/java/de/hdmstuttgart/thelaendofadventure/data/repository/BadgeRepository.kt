package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for interacting with the badge-related data in the database.
 */
interface BadgeRepository {

    /**
     * Gets all the accepted badges for a given user.
     *
     * @param userID the ID of the user to get the badges for
     * @return a [Flow] of [BadgeEntity] objects representing the accepted badges
     */
    fun getAcceptedBadgesByUserID(userID: Int): Flow<BadgeEntity>

    /**
     * Gets all the unaccepted badges for a given user.
     *
     * @param userID the ID of the user to get the badges for
     * @return a [Flow] of [BadgeEntity] objects representing the unaccepted badges
     */
    fun getUnacceptedBadgesByUserID(userID: Int): Flow<BadgeEntity>

    /**
     * Gets the progress of a specific badge for a specific user.
     *
     * @param userID the ID of the user to get the badge progress for
     * @param badgeID the ID of the badge to get the progress for
     * @return a [Flow] emitting a [Progress] object representing the progress of the badge
     */
    fun getProgressForBadgeByUserID(userID: Int, badgeID: Int): Flow<Progress>

    /**
     * Gets all the completed goals for a specific badge and user.
     *
     * @param userID the ID of the user to get the completed goals for
     * @param badgeID the ID of the badge to get the completed goals for
     * @return a [Flow] of [ActionEntity] objects representing the completed goals
     */
    fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<ActionEntity>

    /**
     * Gets all the uncompleted goals for a specific badge and user.
     *
     * @param userID the ID of the user to get the uncompleted goals for
     * @param badgeID the ID of the badge to get the uncompleted goals for
     * @return a [Flow] of [ActionEntity] objects representing the uncompleted goals
     */
    fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<ActionEntity>

    /**
     * Updates the progress of a specific badge for a specific user.
     *
     * @param userID the ID of the user to update the badge progress for
     * @param badgeID the ID of the badge to update the progress for
     * @param goalNumber the new goal number to set for the badge progress
     */
    suspend fun updateBadgeProgressByUserID(userID: Int, badgeID: Int, goalNumber: Int)
}
