package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.BadgeDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

class OfflineBadgeRepository(private val badgeDao: BadgeDao) : BadgeRepository {

    override fun getAcceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>> =
        badgeDao.getAcceptedBadgesByUserID(userID)

    override fun getUnacceptedBadgesByUserID(userID: Int): Flow<List<BadgeEntity>> =
        badgeDao.getUnacceptedBadgesByUserID(userID)

    override fun getProgressForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<Progress> = badgeDao.getProgressForBadgeByUserID(userID, badgeID)

    override fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getCompletedGoalsForBadgeByUserID(userID, badgeID)

    override fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getUncompletedGoalsForBadgeByUserID(userID, badgeID)

    override suspend fun updateBadgeProgressByUserID(userID: Int, badgeID: Int, goalNumber: Int) {
        val badgeProgress = badgeDao.getProgressForBadgeByUserID(userID, badgeID)

        badgeProgress.collect {
            val currentGoalNumber = it.currentGoalNumber
            val targetGoalNumber = it.targetGoalNumber

            if (currentGoalNumber != targetGoalNumber) {
                badgeDao.updateBadgeProgressByUserID(userID, badgeID, goalNumber)
            }
        }
    }

    private suspend fun checkForBadgeProgress(userID: Int, questID: Int) {
        val allBadges = getAcceptedBadgesByUserID(userID)
        allBadges.collect { badgesList ->
            badgesList.forEach { badge ->

            }
        }
    }

    override suspend fun assignAllBadgesToUser(userID: Int) {
        val badges = badgeDao.getUnacceptedBadgesByUserID(userID).toList().flatten()
        val badgesIDs = badges.map { it.badgeID }
        badgeDao.assignAllBadgesToUser(userID, badgesIDs)
    }
}
