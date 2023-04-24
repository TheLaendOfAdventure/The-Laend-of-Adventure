package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestProgress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Query(
        "SELECT badge.* FROM badge " +
            "INNER JOIN user_badge ON badge.badgeID = user_badge.badgeID " +
            "WHERE user_badge.userID = :userID"
    )
    fun getAcceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>>

    @Query(
        "SELECT badge.* FROM badge " +
            "LEFT JOIN user_badge ON badge.badgeID = user_badge.badgeID " +
            "AND user_badge.userID = :userID " +
            "WHERE user_badge.userID IS NULL"
    )
    fun getUnacceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>>

    @Query(
        "SELECT user_badge.currentGoalNumber, badge.targetGoalNumber FROM user_badge " +
            "INNER JOIN badge ON user_badge.badgeID = badge.badgeID " +
            "WHERE user_badge.userID = :userID AND user_badge.badgeID = :badgeID"
    )
    fun getGoalBadgeProgressForBadgeForUser(userID: Int, badgeID: Int): Flow<QuestProgress>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "INNER JOIN user_badge ON user_badge.badgeID = badgeGoal.badgeID " +
            "WHERE user_badge.currentGoalNumber >= badgeGoal.goalNumber " +
            "AND user_badge.userID = :userID AND badgeGoal.badgeID = :badgeID"
    )
    fun getCompletedGoalsForBadgeAndUser(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "INNER JOIN user_badge ON user_badge.badgeID = badgeGoal.badgeID " +
            "WHERE user_badge.currentGoalNumber < badgeGoal.goalNumber " +
            "AND user_badge.userID = :userID AND badgeGoal.badgeID = :badgeID"
    )
    fun getUncompletedGoalsForBadgeAndUser(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "UPDATE user_badge SET currentGoalNumber = :goalNumber " +
            "WHERE badgeID = :badgeID AND userID = :userID"
    )
    suspend fun updateBadgeProgress(userID: Int, badgeID: Int, goalNumber: Int)
}
