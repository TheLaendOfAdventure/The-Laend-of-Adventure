package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.*
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.*
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Suppress("TooManyFunctions")
@Dao
interface QuestDao {

    @Insert
    suspend fun addQuest(quest: QuestEntity): Long

    @Insert
    suspend fun addQuestGoal(questGoal: QuestGoalEntity): Long

    @Query(
        "SELECT dialogPath FROM quest " +
            "WHERE quest.questID = :questID"
    )
    suspend fun getDialogPathByQuestID(questID: Int): String

    @Query(
        "SELECT * FROM quest " +
            "WHERE quest.questID = :questID"
    )
    suspend fun getQuestByQuestID(questID: Int): QuestEntity

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
        "SELECT user_quest.*, quest.name, quest.targetGoalNumber, quest.description FROM user_quest " + // ktlint-disable max-line-length
            "LEFT JOIN quest ON user_quest.questID = quest.questID " +
            "WHERE user_quest.userID = :userID"
    )
    fun getQuestsWithDetailsByUserID(userID: Int): Flow<List<QuestDetails>>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN questGoal ON action.actionID = questGoal.actionID " +
            "INNER JOIN user_quest ON user_quest.questID = questGoal.questID " +
            "WHERE user_quest.currentGoalNumber >= questGoal.goalNumber " +
            "AND user_quest.userID = :userID AND questGoal.questID = :questID"
    )
    fun getCompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    @Query(
        "SELECT action.* From action " +
            "INNER JOIN questGoal ON action.actionID = questGoal.actionID " +
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
        "INSERT INTO user_quest (userID, questID, currentGoalNumber)" +
            "VALUES (:userID, :questID, 0)"
    )
    suspend fun assignQuestToUser(userID: Int, questID: Int)

    @Query(
        "SELECT quest.questID, questGoal.questGoalID," +
            "user_quest.currentGoalNumber, location.longitude, location.latitude " +
            "FROM quest " +
            "JOIN user_quest ON quest.questID = user_quest.questID " +
            "JOIN questGoal ON questGoal.questID = quest.questID " +
            "JOIN location ON questGoal.actionID = location.actionID " +
            "WHERE user_quest.userID = :userID " +
            "AND user_quest.currentGoalNumber < quest.targetGoalNumber " +
            "AND user_quest.currentGoalNumber = questGoal.goalNumber"
    )
    fun getLocationForAcceptedQuestsByUserID(userID: Int): Flow<List<LocationGoal>>

    @Query(
        "SELECT questGoal.questID , questGoal.goalNumber, " +
            "riddle.*, riddleAnswers.answer AS possibleAnswers FROM riddle " +
            "JOIN riddleAnswers ON riddleAnswers.actionID = riddle.actionID " +
            "JOIN questGoal ON questGoal.actionID = riddle.actionID " +
            "JOIN user_quest ON user_quest.questID = questGoal.questID " +
            "WHERE user_quest.userID = :userID " +
            "AND user_quest.currentGoalNumber  = questGoal.goalNumber"
    )
    fun getRiddleForAcceptedQuestsByUserID(userID: Int):
        Flow<List<RiddleDetails>>

    @Query(
        "SELECT achievement.questID " +
            "FROM achievement " +
            "JOIN action ON achievement.actionID = action.actionID " +
            "JOIN badgeGoal ON action.actionID = badgeGoal.actionID " +
            "JOIN user_badge ON badgeGoal.badgeGoalID = user_badge.badgeGoalID " +
            "WHERE badgeGoal.badgeID = :badgeID " +
            "AND user_badge.userID = :userID"
    )
    fun getQuestForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<Int>>

    @Query(
        "SELECT action.description " +
            "FROM action " +
            "INNER JOIN questGoal ON action.actionID = questGoal.actionID " +
            "WHERE questGoal.questID = :questID " +
            "ORDER BY questGoal.goalNumber "
    )
    fun getAllActionDescriptionsByQuestID(questID: Int): Flow<List<String>>

    @Query(
        "SELECT imagePath " +
            "FROM quest " +
            "WHERE quest.questID = :questID "
    )
    suspend fun getQuestImageByQuestID(questID: Int): String

    @Query(
        "SELECT name FROM action " +
            "JOIN questGoal ON questGoal.actionID = action.actionID " +
            "WHERE questGoal.questID = :questID " +
            "AND goalNumber = :goalNumber"
    )
    suspend fun getNameByQuestByGoal(questID: Int, goalNumber: Int): String

    @Query(
        "SELECT location.* FROM location " +
            "JOIN action ON action.actionID = location.actionID " +
            "JOIN questGoal ON questGoal.actionID = action.actionID " +
            "WHERE questGoal.goalNumber = :goalNumber AND questGoal.questID = :questID"
    )
    suspend fun getLocationByQuestByGoal(questID: Int, goalNumber: Int): Location

    @Query(
        "SELECT location.longitude, location.latitude " +
            "FROM quest " +
            "JOIN user_quest ON quest.questID = user_quest.questID " +
            "JOIN questGoal ON questGoal.questID = quest.questID " +
            "JOIN location ON questGoal.actionID = location.actionID " +
            "WHERE user_quest.userID = :userID " +
            "AND user_quest.currentGoalNumber < quest.targetGoalNumber " +
            "AND user_quest.currentGoalNumber = questGoal.goalNumber"
    )
    suspend fun getOnlyLocationForAcceptedQuestsByUserID(userID: Int): List<Location>
}
