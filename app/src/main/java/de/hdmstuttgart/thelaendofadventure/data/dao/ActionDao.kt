package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import kotlinx.coroutines.flow.Flow

interface ActionDao {
    @Query(
        "SELECT location.* FROM location" +
            "INNER JOIN action ON action.actionID = location.actionID" +
            "WHERE location.actionID = :actionID"
    )
    fun getLocationForAction(actionID: Int): Flow<LocationEntity>

    @Query(
        "SELECT quest.* FROM quest" +
            "INNER JOIN action ON action.actionID = achievement.actionID" +
            "WHERE achievement.actionID = :actionID"
    )
    fun getAchievementForAction(actionID: Int): Flow<LocationEntity>

    @Query(
        "SELECT * FROM riddle" +
            "JOIN riddleAnswers ON riddle.actionID = riddleAnswers.actionID" +
            "WHERE riddle.actionID = :actionID"
    )
    fun getRiddleAndAnserwsForAction(actionID: Int):
        Flow<Map<RiddleEntity, List<RiddleAnswersEntity>>>
}
