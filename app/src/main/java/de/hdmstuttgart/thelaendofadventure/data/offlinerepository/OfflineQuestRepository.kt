package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.QuestDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.*
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Suppress("TooManyFunctions")
class OfflineQuestRepository(private val questDao: QuestDao) : QuestRepository {
    override suspend fun getDialogPathByQuestID(questID: Int): String {
        return questDao.getDialogPathByQuestID(questID)
    }

    override fun getAcceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>> =
        questDao.getAcceptedQuestsByUserID(userID)

    override fun getUnacceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>> =
        questDao.getUnacceptedQuestsByUserID(userID)

    override fun getQuestsWithDetailsByUserID(userID: Int):
        Flow<List<QuestDetails>> =
        questDao.getQuestsWithDetailsByUserID(userID)

    override fun getProgressForQuestByUserID(userID: Int, questID: Int):
        Flow<Progress> =
        questDao.getProgressForQuestByUserID(userID, questID)

    override fun getCompletedGoalsForQuestByUserID(userID: Int, questID: Int):
        Flow<List<ActionEntity>> =
        questDao.getCompletedGoalsForQuestByUserID(userID, questID)

    override fun getRiddleForAcceptedQuestsByUserID(userID: Int):
        Flow<List<RiddleDetails>> =
        questDao.getRiddleForAcceptedQuestsByUserID(userID)

    override fun getUncompletedGoalsForQuestByUserID(userID: Int, questID: Int):
        Flow<List<ActionEntity>> =
        questDao.getUncompletedGoalsForQuestByUserID(userID, questID)

    override suspend fun getQuestForBadgeByUserID(userID: Int, badgeID: Int): Flow<List<Int>> =
        questDao.getQuestForBadgeByUserID(userID, badgeID)

    override suspend fun updateAndCheckQuestProgressByUserID(
        userID: Int,
        questID: Int,
        goalNumber: Int
    ): Boolean {
        questDao.updateQuestProgressByUserID(userID, questID, goalNumber)
        return isQuestCompleted(userID, questID)
    }

    private suspend fun isQuestCompleted(userID: Int, questID: Int): Boolean {
        val questProgress = questDao.getProgressForQuestByUserID(userID, questID).first()

        val currentGoalNumber = questProgress.currentGoalNumber
        val targetGoalNumber = questProgress.targetGoalNumber

        return currentGoalNumber == targetGoalNumber
    }

    override suspend fun assignQuestToUser(userID: Int, questID: Int) {
        questDao.assignQuestToUser(userID, questID)
    }

    override fun getLocationForAcceptedQuestsByUserID(userID: Int): Flow<List<LocationGoal>> {
        return questDao.getLocationForAcceptedQuestsByUserID(userID)
    }

    override fun getAllActionDescriptionsByQuestID(questID: Int): Flow<List<String>> {
        return questDao.getAllActionDescriptionsByQuestID(questID)
    }

    override suspend fun getQuestImageByQuestID(questID: Int): String {
        return questDao.getQuestImageByQuestID(questID)
    }

    override suspend fun getQuestByQuestID(questID: Int): QuestEntity {
        return questDao.getQuestByQuestID(questID)
    }

    override suspend fun getNameByQuestByGoal(questID: Int, goalNumber: Int): String {
        return questDao.getNameByQuestByGoal(questID, goalNumber)
    }

    override suspend fun getLocationByQuestByGoal(questID: Int, goalNumber: Int): Location {
        return questDao.getLocationByQuestByGoal(questID, goalNumber)
    }

    override suspend fun getOnlyLocationForAcceptedQuestsByUserID(userID: Int): List<Location> {
        return questDao.getOnlyLocationForAcceptedQuestsByUserID(userID)
    }
}
