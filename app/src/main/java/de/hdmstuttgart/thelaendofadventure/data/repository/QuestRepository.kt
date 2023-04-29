package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing and modifying Quest related data from the database.
 */
interface QuestRepository {

    /**
     * Retrieves a list of all quests accepted by the user with the given ID.
     *
     * @param userID ID of the user whose accepted quests should be retrieved.
     * @return A [Flow] emitting a list of [QuestEntity] objects representing the accepted quests.
     */
    fun getAcceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    /**
     * Retrieves a list of all quests that have not been accepted by the user with the given ID.
     *
     * @param userID ID of the user whose unaccepted quests should be retrieved.
     * @return A [Flow] emitting a list of [QuestEntity] objects representing the unaccepted quests.
     */
    fun getUnacceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>>

    /**
     * Retrieves the progress of the current goal for the given quest and user.
     *
     * @param userID ID of the user whose quest progress should be retrieved.
     * @param questID ID of the quest whose progress should be retrieved.
     * @return A [Flow] emitting a [Progress] object containing the current and target goal numbers.
     */
    fun getProgressForQuestByUserID(userID: Int, questID: Int): Flow<Progress>

    /**
     * Retrieves a list of all completed goals for the given quest and user.
     *
     * @param userID ID of the user whose completed goals should be retrieved.
     * @param questID ID of the quest whose completed goals should be retrieved.
     * @return A [Flow] emitting a list of [ActionEntity] objects representing the completed goals.
     */
    fun getCompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    /**
     * Retrieves a list of all uncompleted goals for the given quest and user.
     *
     * @param userID ID of the user whose uncompleted goals should be retrieved.
     * @param questID ID of the quest whose uncompleted goals should be retrieved.
     * @return A [Flow] emitting a list of [ActionEntity] objects representing the uncompleted goals.
     */
    fun getUncompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    /**
     * Updates the progress of the current goal for the given quest and user.
     *
     * @param userID ID of the user whose quest progress should be updated.
     * @param questID ID of the quest whose progress should be updated.
     * @param goalNumber The new goal number to set.
     */
    suspend fun updateQuestProgressByUserID(userID: Int, questID: Int, goalNumber: Int)
}
