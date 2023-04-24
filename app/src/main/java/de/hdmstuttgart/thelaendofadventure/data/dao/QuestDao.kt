package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestProgress
import de.hdmstuttgart.thelaendofadventure.data.entity.*
import kotlinx.coroutines.flow.Flow

interface QuestDao {

    @Query(
        "SELECT quest.* FROM quest" +
            "INNER JOIN user_quest ON quest.questID = user_quest.questID" +
            "WHERE user_quest.userID = :userID"
    )
    fun getAcceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    @Query(
        "SELECT quest.* FROM quest" +
            "LEFT JOIN user_quest ON quest.questID = user_quest.questID" +
            "AND user_quest.userID = :userID" +
            "WHERE user_quest.userID IS NULL"
    )
    fun getUnacceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    @Query(
        "SELECT user_quest.currentGoalNumber, quest.targetGoalNumber FROM user_quest" +
            "INNER JOIN quest ON user_quest.questID = quest.questID" +
            "WHERE user_quest.userID = :userID AND user_quest.questID = :questID"
    )
    fun getGoalQuestProgressForQuestForUser(userID: Int, questID: Int): Flow<QuestProgress>

    @Query(
        "SELECT action.* From action" +
            "INNER JOIN questGoal ON action.actionID = questGoal.actionID" +
            "INNER JOIN user_quest ON user_quest.questID = questGoal.questID" +
            "WHERE user_quest.currentGoalNumber >= questGoal.goalNumber" +
            "AND user_quest.userID = :userID AND questGoal.questID = questID"
    )
    fun getCompletedGoalsForQuestAndUser(userID: Int, questID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT action.* From action" +
            "INNER JOIN questGoal ON action.actionID = questGoal.actionID" +
            "INNER JOIN user_quest ON user_quest.questID = questGoal.questID" +
            "WHERE user_quest.currentGoalNumber < questGoal.goalNumber" +
            "AND user_quest.userID = :userID AND questGoal.questID = questID"
    )
    fun getUncompletedGoalsForQuestAndUser(userID: Int, questID: Int): Flow<List<ActionEntity>>

    @Query(
        "UPDATE user_quest SET currentGoalNumber = :goalNumber" +
            "WHERE questID = :questID AND userID = :userID"
    )
    suspend fun updateQuestProgress(userID: Int, questID: Int, goalNumber: Int)
}
