package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Query(
        "SELECT badge.*, COUNT(badgeGoal.badgeGoalID) AS targetGoalNumber, " +
            "COUNT(CASE WHEN user_badge.isCompleted = 1 THEN 1 END) AS currentGoalNumber " +
            "FROM badge " +
            "LEFT JOIN badgeGoal ON badge.badgeID = badgeGoal.badgeID " +
            "LEFT JOIN user_badge ON badgeGoal.badgeGoalID = user_badge.badgeGoalID " +
            "WHERE user_badge.userID = :userID " +
            "GROUP BY badge.badgeID " +
            "HAVING COUNT(badgeGoal.badgeGoalID) = COUNT(CASE WHEN user_badge.isCompleted = 1 THEN 1 END)"
    )
    fun getCompletedBadgesDetailsByUserID(userID: Int): Flow<List<BadgeDetails>>

    @Query(
        "SELECT badge.*, COUNT(badgeGoal.badgeGoalID) AS targetGoalNumber, " +
            "COUNT(CASE WHEN user_badge.isCompleted = 1 THEN 0 END) AS currentGoalNumber " +
            "FROM badge " +
            "LEFT JOIN badgeGoal ON badge.badgeID = badgeGoal.badgeID " +
            "LEFT JOIN user_badge ON badgeGoal.badgeGoalID = user_badge.badgeGoalID " +
            "WHERE user_badge.userID = :userID " +
            "GROUP BY badge.badgeID " +
            "HAVING COUNT(badgeGoal.badgeGoalID) > COUNT(CASE WHEN user_badge.isCompleted = 1 THEN 1 END)"
    )
    fun getUnCompletedBadgesDetailsByUserID(userID: Int): Flow<List<BadgeDetails>>

    @Query(
        "SELECT * FROM badge " +
            "WHERE badge.badgeID = :badgeID"
    )
    suspend fun getBadgeByBadgeID(badgeID: Int): BadgeEntity

    @Query(
        "SELECT COUNT(CASE WHEN user_badge.isCompleted = 1 THEN 1 END) AS currentGoalNumber, " +
            "COUNT(DISTINCT user_badge.badgeGoalID) as targetGoalNumber " +
            "FROM user_badge " +
            "JOIN badgeGoal ON user_badge.badgeGoalID = badgeGoal.badgeGoalID " +
            "WHERE user_badge.userID = :userID AND user_badge.badgeID = :badgeID " +
            "GROUP BY user_badge.userID, user_badge.badgeID"
    )
    fun getProgressForBadgeByUserID(userID: Int, badgeID: Int): Flow<Progress>

    @Query(
        "SELECT action.* FROM action " +
            "JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "JOIN user_badge ON badgeGoal.badgeGoalID = user_badge.badgeGoalID " +
            "WHERE user_badge.userID = :userID AND user_badge.badgeID = :badgeID AND user_badge.isCompleted = 1"
    )
    fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT action.* FROM action " +
            "JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "JOIN user_badge ON badgeGoal.badgeGoalID = user_badge.badgeGoalID " +
            "WHERE user_badge.userID = :userID AND user_badge.badgeID = :badgeID AND user_badge.isCompleted = 0"
    )
    fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT user_badge.* " +
            "FROM user_badge " +
            "JOIN badgeGoal ON user_badge.badgeID = badgeGoal.badgeID " +
            "JOIN achievement ON badgeGoal.actionID = achievement.actionID " +
            "WHERE user_badge.userID = :userID " +
            "AND achievement.questID = :questID " +
            "AND badgeGoal.actionID = achievement.actionID"
    )
    fun getUserBadgesByUserIDAndQuestID(userID: Int, questID: Int): Flow<List<UserBadgeEntity>>

    @Query(
        "UPDATE user_badge SET isCompleted = 1 " +
            "WHERE badgeID = :badgeID " +
            "AND userID = :userID " +
            "AND badgeGoalID = :badgeGoalID "
    )
    suspend fun completeBadgeGoalByUserID(userID: Int, badgeID: Int, badgeGoalID: Int)

    @Query(
        "INSERT INTO user_badge (userID, badgeID, badgeGoalID) " +
            "SELECT :userID, badgeID, badgeGoalID " +
            "FROM badgeGoal"
    )
    suspend fun assignAllBadgesToUser(userID: Int)

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
