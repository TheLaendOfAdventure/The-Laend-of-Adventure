package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query(
        "SELECT quest.* FROM quest " +
            "INNER JOIN user_quest ON quest.questID = user_quest.questID " +
            "WHERE user_quest.userID = :userID"
    )
    fun getAcceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    @Query(
        "SELECT quest.* FROM quest " +
            "LEFT JOIN user_quest ON quest.questID = user_quest.questID " +
            "AND user_quest.userID = :userID " +
            "WHERE user_quest.userID IS NULL"
    )
    fun getUnacceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    @Query(
        "SELECT user_quest.currentGoalNumber, quest.targetGoalNumber FROM user_quest " +
            "INNER JOIN quest ON user_quest.questID = quest.questID " +
            "WHERE user_quest.userID = :userID AND user_quest.questID = :questID"
    )
    fun getProgressForQuestByUserID(userID: Int, questID: Int): Flow<Progress>

    @Query(
        "SELECT [action].* From [action] " +
            "INNER JOIN questGoal ON [action].actionID = questGoal.actionID " +
            "INNER JOIN user_quest ON user_quest.questID = questGoal.questID " +
            "WHERE user_quest.currentGoalNumber >= questGoal.goalNumber " +
            "AND user_quest.userID = :userID AND questGoal.questID = :questID"
    )
    fun getCompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT [action].* From [action] " +
            "INNER JOIN questGoal ON [action].actionID = questGoal.actionID " +
            "INNER JOIN user_quest ON user_quest.questID = questGoal.questID " +
            "WHERE user_quest.currentGoalNumber < questGoal.goalNumber " +
            "AND user_quest.userID = :userID AND questGoal.questID = :questID"
    )
    fun getUncompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    @Query(
        "UPDATE user_quest SET currentGoalNumber = :goalNumber " +
            "WHERE questID = :questID AND userID = :userID"
    )
    suspend fun updateQuestProgressByUserID(userID: Int, questID: Int, goalNumber: Int)

    @Query(
        "INSERT INTO user_quest (userID, questID)" +
            "VALUES (:userID, :questID)"
    )
    suspend fun assignQuestToUser(userID: Int, questID: Int)
}
