package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.BadgeDao
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Progress
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserBadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import kotlinx.coroutines.flow.Flow

class OfflineBadgeRepository(private val badgeDao: BadgeDao) : BadgeRepository {

    /**
     * Gets all completed badges for a given user.
     *
     * @param userID the ID of the user to get the badges for
     * @return a [Flow] emitting a list of [BadgeDetails] objects representing the completed badges
     */
    override fun getCompletedBadgesDetailsByUserID(userID: Int):
        Flow<List<BadgeDetails>> = badgeDao.getCompletedBadgesDetailsByUserID(userID)

    /**
     * Gets all uncompleted badges for a given user.
     *
     * @param userID the ID of the user to get the badges for
     * @return a [Flow] emitting a list of [BadgeDetails] objects representing the uncompleted badges
     */
    override fun getUnCompletedBadgesDetailsByUserID(userID: Int):
        Flow<List<BadgeDetails>> = badgeDao.getUnCompletedBadgesDetailsByUserID(userID)

    override fun getProgressForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<Progress> = badgeDao.getProgressForBadgeByUserID(userID, badgeID)

    override fun getCompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getCompletedGoalsForBadgeByUserID(userID, badgeID)

    override fun getUncompletedGoalsForBadgeByUserID(userID: Int, badgeID: Int):
        Flow<List<ActionEntity>> = badgeDao.getUncompletedGoalsForBadgeByUserID(userID, badgeID)

    override fun getUserBadgeGoalsByQuestID(userID: Int, questID: Int):
        Flow<List<UserBadgeEntity>> = badgeDao.getUserBadgesByUserIDAndQuestID(userID, questID)

    override suspend fun completeBadgeGoalByUserID(userID: Int, badgeID: Int, badgeGoalID: Int) {
        badgeDao.completeBadgeGoalByUserID(userID, badgeID, badgeGoalID)
    }

    override suspend fun assignAllBadgesToUser(userID: Int) {
        badgeDao.assignAllBadgesToUser(userID)
    }

    override suspend fun getBadgeByBadgeID(badgeID: Int): BadgeEntity =
        badgeDao.getBadgeByBadgeID(badgeID)

    override suspend fun getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID: Int):
        BadgeGoalEntity = badgeDao.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID)
}
