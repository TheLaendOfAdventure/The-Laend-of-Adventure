package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.ActionDao
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.ActionRepository
import kotlinx.coroutines.flow.Flow

class OfflineActionRepository(private val actionDao: ActionDao) : ActionRepository {
    override fun getLocationByActionID(actionID: Int): Flow<LocationEntity> =
        actionDao.getLocationByActionID(actionID)

    override fun getAchievementByActionID(actionID: Int): Flow<QuestEntity> =
        actionDao.getAchievementByActionID(actionID)

    override fun getRiddleAndAnswersByActionID(actionID: Int):
        Flow<Map<RiddleEntity, List<RiddleAnswersEntity>>> =
        actionDao.getRiddleAndAnswersByActionID(actionID)

    override suspend fun getDialogPath(userID: Int, goalNumber: Int, questID: Int): String? {
        return actionDao.getDialogPath(userID, goalNumber, questID)
    }
}
