package de.hdmstuttgart.thelaendofadventure.data.repository

import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.LocationGoal
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing and modifying Quest related data from the database.
 */
@Suppress("TooManyFunctions")
interface QuestRepository {

    /**
     * Retrieves the dialog path for a given quest ID.
     *
     * @param questID The ID of the quest.
     * @return The dialog path associated with the quest ID, or an empty string if no dialog path is found.
     */
    suspend fun getDialogPathByQuestID(questID: Int): String

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
     * Retrieves a mapping of RiddleEntity objects to a list of RiddleAnswersEntity objects
     * for the accepted quests of a given userID.
     *
     * @param userID The ID of the user for which to retrieve the riddles for accepted quests.
     * @return A [Flow] emitting a mapping of [RiddleEntity] objects to a list of [RiddleAnswersEntity] objects.
     *         The mapping represents the riddles and their corresponding answers for the accepted quests.
     */
    fun getRiddleForAcceptedQuestsByUserID(userID: Int):
        Flow<List<RiddleDetails>>

    /**
     * Retrieves a list of all uncompleted goals for the given quest and user.
     *
     * @param userID ID of the user whose uncompleted goals should be retrieved.
     * @param questID ID of the quest whose uncompleted goals should be retrieved.
     * @return A [Flow] emitting a list of [ActionEntity] objects representing the uncompleted goals.
     */
    fun getUncompletedGoalsForQuestByUserID(userID: Int, questID: Int): Flow<List<ActionEntity>>

    /**
     * Retrieves a list of all quest accepted by user with details.
     *
     * @param userID ID of the user whose uncompleted goals should be retrieved.
     * @return A [Flow] emitting a list of [QuestDetails] objects representing the accepted quests.
     */
    fun getQuestsWithDetailsByUserID(userID: Int): Flow<List<QuestDetails>>

    /**
     * Retrieves a list of quest IDs associated with a specific badge and user.
     *
     * @param userID The ID of the user.
     * @param badgeID The ID of the badge.
     * @return A list of quest IDs associated with the specified badge and user.
     */
    suspend fun getQuestForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<Int>>

    /**
     * Updates the progress of the current goal for the given quest and user and checks if the quest is completed.
     *
     * @param userID ID of the user whose quest progress should be updated.
     * @param questID ID of the quest whose progress should be updated.
     * @param goalNumber The new goal number to set.
     * @return True if the quest is completed, otherwise False.
     */
    suspend fun updateAndCheckQuestProgressByUserID(userID: Int, questID: Int, goalNumber: Int):
        Boolean

    /**
     * Accepts a quest for a user with the given user ID.
     *
     * @param userID The ID of the user accepting the quest.
     * @param questID The ID of the quest being accepted.
     */
    suspend fun assignQuestToUser(userID: Int, questID: Int)

    /**
     * Retrieves a list of [LocationGoal] of all active quests of a given user whose current goal is a location action.
     *
     * @param userID The ID of the user
     */
    fun getLocationForAcceptedQuestsByUserID(userID: Int): Flow<List<LocationGoal>>

    /**
     * Retrieves a list of [String] of all quest actions descriptions.
     *
     * @param questID The ID of the quest
     */
    fun getAllActionDescriptionsByQuestID(questID: Int): Flow<List<String>>

    /**
     * Retrieves a [String] of the quest image path.
     *
     * @param questID The ID of the quest
     */
    suspend fun getQuestImageByQuestID(questID: Int): String?

    /**
     * Retrieves a QuestEntity object from the database based on the provided quest ID.
     *
     * @param questID The ID of the quest to retrieve.
     * @return The QuestEntity object representing the quest with the specified ID, or null if not found.
     */
    suspend fun getQuestByQuestID(questID: Int): QuestEntity

    /**
     * Retrieves the name of a goal based on the quest ID and goal number.
     *
     * @param questID The ID of the quest.
     * @param goalNumber The goal number of the quest.
     * @return The name of the quest.
     */
    suspend fun getNameByQuestByGoal(questID: Int, goalNumber: Int): String

    /**
     * Retrieves the Location Entity for specific goal.
     *
     * @param questID The ID of the quest.
     * @param goalNumber The goal number of the quest.
     * @return Location Entity.
     */
    suspend fun getLocationByQuestByGoal(questID: Int, goalNumber: Int): LocationEntity
}
