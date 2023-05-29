package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for action-related data in the database.
 * Provides methods for accessing and modifying actions data.
 */
interface ActionRepository {

    /**
     * Retrieves the location associated with the given [actionID].
     *
     * @param actionID The ID of the action to retrieve the location for.
     * @return A [Flow] of the [LocationEntity] associated with the given [actionID].
     */
    fun getLocationByActionID(actionID: Int): Flow<LocationEntity>

    /**
     * Retrieves the achievement associated with the given [actionID].
     *
     * @param actionID The ID of the action to retrieve the achievement for.
     * @return A [Flow] of the [QuestEntity] associated with the given [actionID].
     */
    fun getAchievementByActionID(actionID: Int): Flow<QuestEntity>

    /**
     * Retrieves the riddle and corresponding answers associated with the given [actionID].
     *
     * @param actionID The ID of the action to retrieve the riddle and answers for.
     * @return A [Flow] of a [Map] of [RiddleEntity] to a [List] of [RiddleAnswersEntity]
     * associated with the given [actionID].
     */
    fun getRiddleAndAnswersByActionID(actionID: Int):
        Flow<Map<RiddleEntity, List<RiddleAnswersEntity>>>

    /**
     * Retrieves the dialog path for a specific quest goal, quest and user.
     *
     * @param userID The ID of the user.
     * @param goalNumber The goal number of the quest.
     * @param questID The ID of the quest.
     * @return The dialog path associated with the given quest goal as a String.
     */
    suspend fun getDialogPath(userID: Int, goalNumber: Int, questID: Int): String?
}
