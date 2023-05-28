package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface ActionDao {
    @Query(
        "SELECT location.* FROM location " +
            "INNER JOIN action ON action.actionID = location.actionID " +
            "WHERE location.actionID = :actionID"
    )
    fun getLocationByActionID(actionID: Int): Flow<LocationEntity>

    @Query(
        "SELECT quest.* FROM quest " +
            "INNER JOIN achievement ON achievement.questID = quest.questID " +
            "INNER JOIN action ON action.actionID = achievement.actionID " +
            "WHERE achievement.actionID = :actionID"
    )
    fun getAchievementByActionID(actionID: Int): Flow<QuestEntity>

    @Query(
        "SELECT * FROM riddle " +
            "JOIN riddleAnswers ON riddle.actionID = riddleAnswers.actionID " +
            "WHERE riddle.actionID = :actionID"
    )
    fun getRiddleAndAnswersByActionID(actionID: Int):
        Flow<Map<RiddleEntity, List<RiddleAnswersEntity>>>

    @Query(
        "SELECT dialogPath FROM action " +
            "JOIN questGoal ON questGoal.actionID = action.actionID " +
            "JOIN user_quest ON user_quest.questID = questGoal.questID " +
            "WHERE questGoal.questID = :questID " +
            "AND questGoal.goalNumber = :goalNumber " +
            "AND user_quest.userID = :userID "
    )
    suspend fun getDialogPath(userID: Int, goalNumber: Int, questID: Int): String?
}
