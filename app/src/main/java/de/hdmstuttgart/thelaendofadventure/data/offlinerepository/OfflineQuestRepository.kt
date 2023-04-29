package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.QuestDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.flow.Flow

class OfflineQuestRepository(private val questDao: QuestDao) : QuestRepository {

    override fun getAcceptedQuestsByUserID(userID: Int): Flow<QuestEntity> =
        questDao.getAcceptedQuestsByUserID(userID)

    override fun getUnacceptedQuestsByUserID(userID: Int): Flow<QuestEntity> =
        questDao.getUnacceptedQuestsByUserID(userID)

    override fun getProgressForQuestByUserID(userID: Int, questID: Int):
        Flow<Progress> =
        questDao.getProgressForQuestByUserID(userID, questID)

    override fun getCompletedGoalsForQuestByUserID(userID: Int, questID: Int):
        Flow<ActionEntity> =
        questDao.getCompletedGoalsForQuestByUserID(userID, questID)

    override fun getUncompletedGoalsForQuestByUserID(userID: Int, questID: Int):
        Flow<ActionEntity> =
        questDao.getUncompletedGoalsForQuestByUserID(userID, questID)

    override suspend fun updateQuestProgressByUserID(userID: Int, questID: Int, goalNumber: Int) {
        questDao.updateQuestProgressByUserID(userID, questID, goalNumber)
    }

    override suspend fun assignQuestToUser(userID: Int, questID: Int) {
        questDao.assignQuestToUser(userID, questID)
    }
}
