package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.AchievementEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {

    @Insert
    suspend fun addAction(action: ActionEntity): Long

    @Insert
    suspend fun addLocation(location: LocationEntity): Long

    @Insert
    suspend fun addAchievement(achievementEntity: AchievementEntity): Long

    @Insert
    suspend fun addGoal(goalEntity: QuestGoalEntity): Long

    @Query(
        "SELECT location.* FROM location " +
            "INNER JOIN action ON action.actionID = location.actionID " +
            "WHERE location.actionID = :actionID"
    )
    fun getLocationByActionID(actionID: Int): Flow<LocationEntity>?

    @Query(
        "SELECT quest.* FROM quest " +
            "INNER JOIN achievement ON achievement.questID = quest.questID " +
            "INNER JOIN action ON action.actionID = achievement.actionID " +
            "WHERE achievement.actionID = :actionID"
    )
    fun getAchievementByActionID(actionID: Int): Flow<QuestEntity>?

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
