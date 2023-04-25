package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.ActionDao
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.ActionRepository
import kotlinx.coroutines.flow.Flow

class OfflineActionRepository(private val actionDao: ActionDao) : ActionRepository {
    override fun getLocationForAction(actionID: Int): Flow<LocationEntity> =
        actionDao.getLocationForAction(actionID)

    override fun getAchievementForAction(actionID: Int): Flow<QuestEntity> =
        actionDao.getAchievementForAction(actionID)

    override fun getRiddleAndAnswersForAction(actionID: Int):
        Flow<Map<RiddleEntity, List<RiddleAnswersEntity>>> =
        actionDao.getRiddleAndAnswersForAction(actionID)
}
