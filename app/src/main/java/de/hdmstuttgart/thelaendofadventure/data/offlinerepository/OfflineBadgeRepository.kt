package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.BadgeDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class OfflineBadgeRepository(private val badgeDao: BadgeDao) : BadgeRepository {

    override fun getAcceptedBadgesDetailsByUserID(userID: Int): Flow<List<BadgeDetails>> =
        badgeDao.getAcceptedBadgesDetailsByUserID(userID)

    override fun getUnacceptedBadgesByUserID(userID: Int): Flow<List<BadgeDetails>> =
        badgeDao.getUnacceptedBadgesByUserID(userID)

    override fun getProgressForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<Progress> = badgeDao.getProgressForBadgeByUserID(userID, badgeID)

    override fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getCompletedGoalsForBadgeByUserID(userID, badgeID)

    override fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getUncompletedGoalsForBadgeByUserID(userID, badgeID)

    override fun getBadgesByUserIDAndQuestID(userID: Int, questID: Int):
        Flow<List<BadgeDetails>> = badgeDao.getBadgesByUserIDAndQuestID(userID, questID)

    override suspend fun updateBadgeProgressByUserID(userID: Int, badgeID: Int, goalNumber: Int) {
        val badgeProgress = badgeDao.getProgressForBadgeByUserID(userID, badgeID).first()

        val currentGoalNumber = badgeProgress.currentGoalNumber
        val targetGoalNumber = badgeProgress.targetGoalNumber

        if (currentGoalNumber != targetGoalNumber) {
            badgeDao.updateBadgeProgressByUserID(userID, badgeID, goalNumber)
        }
    }

    override suspend fun assignAllBadgesToUser(userID: Int) {
        val badges = badgeDao.getUnacceptedBadgesByUserID(userID).first()
        val badgesIDs = badges.map { it.badgeID }
        badgesIDs.forEach { badgesID ->
            badgeDao.assignAllBadgesToUser(userID, badgesID)
        }
    }

    override suspend fun getBadgeByBadgeID(badgeID: Int): BadgeEntity {
        return badgeDao.getBadgeByBadgeID(badgeID)
    }
}
