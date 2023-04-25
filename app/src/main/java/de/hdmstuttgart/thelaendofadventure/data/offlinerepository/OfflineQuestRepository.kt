package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.QuestDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.flow.Flow

class OfflineQuestRepository(private val questDao: QuestDao) : QuestRepository {

    override fun getAcceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>> =
        questDao.getAcceptedQuestsByUserID(userID)

    override fun getUnacceptedQuestsByUserID(userID: Int): Flow<List<QuestEntity>> =
        questDao.getUnacceptedQuestsByUserID(userID)

    override fun getGoalQuestProgressForQuestForUser(userID: Int, questID: Int):
        Flow<Progress> =
        questDao.getGoalQuestProgressForQuestForUser(userID, questID)

    override fun getCompletedGoalsForQuestAndUser(userID: Int, questID: Int):
        Flow<List<ActionEntity>> =
        questDao.getCompletedGoalsForQuestAndUser(userID, questID)

    override fun getUncompletedGoalsForQuestAndUser(userID: Int, questID: Int):
        Flow<List<ActionEntity>> =
        questDao.getUncompletedGoalsForQuestAndUser(userID, questID)

    override suspend fun updateQuestProgress(userID: Int, questID: Int, goalNumber: Int) {
        questDao.updateQuestProgress(userID, questID, goalNumber)
    }
}
