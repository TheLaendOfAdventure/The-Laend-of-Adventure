package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.BadgeDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import kotlinx.coroutines.flow.Flow

class OfflineBadgeRepository(private val badgeDao: BadgeDao) : BadgeRepository {

    override fun getAcceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>> =
        badgeDao.getAcceptedBadgesByUserID(userID)

    override fun getUnacceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>> =
        badgeDao.getUnacceptedBadgesByUserID(userID)

    override fun getGoalBadgeProgressForBadgeForUser(userID: Int, badgeID: Int):
        Flow<Progress> = badgeDao.getGoalBadgeProgressForBadgeForUser(userID, badgeID)

    override fun getCompletedGoalsForBadgeAndUser(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getCompletedGoalsForBadgeAndUser(userID, badgeID)

    override fun getUncompletedGoalsForBadgeAndUser(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getUncompletedGoalsForBadgeAndUser(userID, badgeID)

    override suspend fun updateBadgeProgress(userID: Int, badgeID: Int, goalNumber: Int) =
        badgeDao.updateBadgeProgress(userID, badgeID, goalNumber)
}
