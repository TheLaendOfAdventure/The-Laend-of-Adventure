package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Query(
        "SELECT badge.*, user_badge.currentGoalNumber FROM badge " +
            "INNER JOIN user_badge ON badge.badgeID = user_badge.badgeID " +
            "WHERE user_badge.userID = :userID"
    )
    fun getAcceptedBadgesDetailsByUserID(userID: Int): Flow<List<BadgeDetails>>

    @Query(
        "SELECT * FROM badge " +
            "WHERE badge.badgeID = :badgeID"
    )
    suspend fun getBadgeByBadgeID(badgeID: Int): BadgeEntity

    @Query(
        "SELECT badge.*, user_badge.currentGoalNumber FROM badge " +
            "LEFT JOIN user_badge ON badge.badgeID = user_badge.badgeID " +
            "AND user_badge.userID = :userID " +
            "WHERE user_badge.userID IS NULL"
    )
    fun getUnacceptedBadgesByUserID(userID: Int): Flow<List<BadgeDetails>>

    @Query(
        "SELECT user_badge.currentGoalNumber, badge.targetGoalNumber FROM user_badge " +
            "INNER JOIN badge ON user_badge.badgeID = badge.badgeID " +
            "WHERE user_badge.userID = :userID AND user_badge.badgeID = :badgeID"
    )
    fun getProgressForBadgeByUserID(userID: Int, badgeID: Int): Flow<Progress>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "INNER JOIN user_badge ON user_badge.badgeID = badgeGoal.badgeID " +
            "WHERE user_badge.currentGoalNumber > badgeGoal.goalNumber " +
            "AND user_badge.userID = :userID AND badgeGoal.badgeID = :badgeID"
    )
    fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "INNER JOIN user_badge ON user_badge.badgeID = badgeGoal.badgeID " +
            "WHERE user_badge.currentGoalNumber <= badgeGoal.goalNumber " +
            "AND user_badge.userID = :userID AND badgeGoal.badgeID = :badgeID"
    )
    fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT badge.*, user_badge.currentGoalNumber FROM badge " +
            "JOIN badgeGoal ON badge.badgeID = badgeGoal.badgeID " +
            "JOIN user_badge ON user_badge.badgeID = badge.badgeID " +
            "JOIN action ON badgeGoal.actionID = action.actionID " +
            "JOIN achievement ON action.actionID = achievement.actionID " +
            "WHERE user_badge.currentGoalNumber = badgeGoal.goalNumber " +
            "AND user_badge.userID = :userID " +
            "AND achievement.questID = :questID"
    )
    fun getBadgesByUserIDAndQuestID(userID: Int, questID: Int): Flow<List<BadgeDetails>>

    @Query(
        "UPDATE user_badge SET currentGoalNumber = :goalNumber " +
            "WHERE badgeID = :badgeID AND userID = :userID"
    )
    suspend fun updateBadgeProgressByUserID(userID: Int, badgeID: Int, goalNumber: Int)

    @Query(
        "INSERT INTO user_badge (userID, badgeID)" +
            "VALUES (:userID, :badgeID)"
    )
    suspend fun assignAllBadgesToUser(userID: Int, badgeID: Int)

    @Query(
        "SELECT badgeGoal.* " +
            "FROM badgeGoal " +
            "JOIN action ON badgeGoal.actionID = action.actionID " +
            "JOIN user_badge ON badgeGoal.badgeID = user_badge.badgeID " +
            "JOIN user ON user_badge.userID = user.userID " +
            "JOIN statTracking ON action.actionID = statTracking.actionID " +
            "WHERE user.userID = :userID " +
            "AND statTracking.goal = user.wrongAnswerCount " +
            "AND statTracking.goalUnit = 'wrongAnswerCount'"
    )
    suspend fun getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID: Int): BadgeGoalEntity
}
