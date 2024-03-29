package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for interacting with the badge-related data in the database.
 */
interface BadgeRepository {

    /**
     * Gets all badges for a given user.
     *
     * @param userID the ID of the user to get the badges for
     * @return a [Flow] emitting a list of [BadgeDetails] objects representing the badges
     */
    fun getBadgesDetailsByUserID(userID: Int): Flow<List<BadgeDetails>>

    /**
     * Gets all the completed goals for a specific badge and user.
     *
     * @param userID the ID of the user to get the completed goals for
     * @param badgeID the ID of the badge to get the completed goals for
     * @return a [Flow] emitting a list of [ActionEntity] objects representing the completed goals
     */
    fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    /**
     * Gets all the uncompleted goals for a specific badge and user.
     *
     * @param userID the ID of the user to get the uncompleted goals for
     * @param badgeID the ID of the badge to get the uncompleted goals for
     * @return a [Flow] emitting a list of [ActionEntity] objects representing the uncompleted goals
     */
    fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    /**
     * Retrieves a list of BadgeEntity objects based on the given userID and questID.
     *
     * @param userID   The ID of the user for which to retrieve the badges.
     * @param questID  The ID of the quest for which to retrieve the badges.
     * @return A [Flow] emitting a list of [BadgeDetails] objects that match the specified criteria.
     */
    fun getUserBadgeGoalsByQuestID(userID: Int, questID: Int): Flow<List<UserBadgeEntity>>

    /**
     * Updates the progress of a specific badge for a specific user.
     *
     * @param userID the ID of the user to update the badge progress for
     * @param badgeID the ID of the badge to update the progress for
     * @param badgeGoalID the new goal number to set for the badge progress
     */
    suspend fun completeBadgeGoalByUserID(userID: Int, badgeID: Int, badgeGoalID: Int)

    /**
     * Assign all Badges to a specific user.
     *
     * @param userID the ID of the user to assign the badges to.
     */
    suspend fun assignAllBadgesToUser(userID: Int)

    /**
     * Retrieves a badge entity by the specified badge ID.
     *
     * @param badgeID The ID of the badge to retrieve.
     * @return The badge entity with the specified badge ID, or null if no badge is found.
     */
    suspend fun getBadgeByBadgeID(badgeID: Int): BadgeEntity?

    /**
     * Get a [BadgeGoalEntity] for a specific user when the number of wrong riddle answers are reached.
     *
     * @param userID the ID of the user to update.
     * @return a [BadgeGoalEntity] when the wrong Riddle-answers is reached for a Badge for the user.
     */
    suspend fun getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID: Int): BadgeGoalEntity?
}
