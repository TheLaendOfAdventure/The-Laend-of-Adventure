package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.AchievementEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.StatTrackingEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BadgeDaoTest {

    private lateinit var badgeDao: BadgeDao
    private lateinit var actionDao: ActionDao
    private lateinit var questDao: QuestDao
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase
    private lateinit var badgeGoal: BadgeGoalEntity
    private lateinit var actionEntity: ActionEntity

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        badgeDao = db.badgeDao()
        actionDao = db.actionDao()
        questDao = db.questDao()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAddBadge() = runBlocking {
        val userID = addUser()
        badgeDao.assignAllBadgesToUser(userID)
        val initialBadgeList = badgeDao.getBadgesDetailsByUserID(userID).first()

        val insertedID = addBadge()
        addBadgeGoal(insertedID)

        assertNotEquals(-1, insertedID)
        assertNotNull(badgeDao.getBadgeByBadgeID(insertedID))
        Log.d("Test", "Ist die Badge da: ${badgeDao.getBadgeByBadgeID(insertedID)}")
        assertFalse(isBadgeIdInList(initialBadgeList, insertedID))

        badgeDao.assignAllBadgesToUser(userID)
        val newInitialList = badgeDao.getBadgesDetailsByUserID(userID).first()
        Log.d("Test", "Liste: $newInitialList")
        assertTrue(isBadgeIdInList(newInitialList, insertedID))
    }

    @Test
    fun testAddBadgeGoal() = runBlocking {
        val insertedID = addBadge()
        val insertBadgeGoalID = addBadgeGoal(insertedID)
        assertNotEquals(-1, insertBadgeGoalID)
        val insertBadgeGoal = badgeDao.getBadgeByBadgeGoalID(insertBadgeGoalID)
        assertEquals(badgeGoal, insertBadgeGoal)
    }

    @Test
    fun testGetBadgeByBadgeGoalID_returnBadgeGoalEntity() = runBlocking {
        val insertedID = addBadge()
        val insertBadgeGoalID = addBadgeGoal(insertedID)
        assertNotEquals(-1, insertBadgeGoalID)
        val insertBadgeGoal = badgeDao.getBadgeByBadgeGoalID(insertBadgeGoalID)
        assertEquals(badgeGoal, insertBadgeGoal)
    }

    @Test
    fun testGetCompletedBadgesDetailsByUserID_returnsBadgeDetailsList() = runBlocking {
        val userID = addUser()
        val badgeDetailsListEmpty = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(0, badgeDetailsListEmpty.size)

        val insertBadgeID = addBadge()
        val insertBadgeGoalID = addBadgeGoal(insertBadgeID)
        badgeDao.assignAllBadgesToUser(userID)
        badgeDao.completeBadgeGoalByUserID(userID, insertBadgeID, insertBadgeGoalID)

        val badgeDetailsList = badgeDao.getCompletedGoalsForBadgeByUserID(userID, insertBadgeID).first()
        assertEquals(1, badgeDetailsList.size)
    }

    @Test
    fun testGetBadgeByBadgeID_validBadgeID_returnsBadgeEntity() = runBlocking {
        val badgeID = addBadge()
        val badgeEntity = badgeDao.getBadgeByBadgeID(badgeID)
        assertEquals(badgeID, badgeEntity?.badgeID)
    }

    @Test
    fun testGetBadgeByBadgeID_invalidBadgeID_returnsNull() = runBlocking {
        val badgeID = -1
        val badgeEntity = badgeDao.getBadgeByBadgeID(badgeID)
        assertNull(badgeEntity)
    }

    @Test
    fun testGetCompletedGoalsForBadgeByUserID_validUserIDAndBadgeID_returnsActionEntityList() =
        runBlocking {
            val userID = addUser()
            val insertBadgeID = addBadge()
            val insertBadgeGoalID = addBadgeGoal(insertBadgeID)
            badgeDao.assignAllBadgesToUser(userID)
            val actionEntityListEmpty =
                badgeDao.getCompletedGoalsForBadgeByUserID(userID, insertBadgeID).first()
            assertEquals(0, actionEntityListEmpty.size)

            badgeDao.completeBadgeGoalByUserID(userID, insertBadgeID, insertBadgeGoalID)

            val actionEntityList =
                badgeDao.getCompletedGoalsForBadgeByUserID(userID, insertBadgeID).first()
            assertEquals(1, actionEntityList.size)
            assertEquals(actionEntity, actionEntityList[0])
        }

    @Test
    fun testGetUncompletedGoalsForBadgeByUserID_validUserIDAndBadgeID_returnsActionEntityList() =
        runBlocking {
            val userID = addUser()
            val insertBadgeID = addBadge()
            val actionEntityListEmpty =
                badgeDao.getUncompletedGoalsForBadgeByUserID(userID, insertBadgeID).first()
            assertEquals(0, actionEntityListEmpty.size)

            addBadgeGoal(insertBadgeID)
            badgeDao.assignAllBadgesToUser(userID)
            val actionEntityList =
                badgeDao.getUncompletedGoalsForBadgeByUserID(userID, insertBadgeID).first()
            assertEquals(1, actionEntityList.size)
            assertEquals(actionEntity, actionEntityList[0])

        }

    @Test
    fun testGetUserBadgesByUserIDAndQuestID_validUserIDAndQuestID_returnsBadgeDetailsList() =
        runBlocking {
            val userID = addUser()
            val questID = addQuest()
            val insertBadgeID = addBadge()
            val insertBadgeGoalID = addBadgeGoal(insertBadgeID)
            badgeDao.assignAllBadgesToUser(userID)
            val badgeDetailsListEmpty = badgeDao.getUserBadgesByUserIDAndQuestID(userID, questID).first()
            assertEquals(0, badgeDetailsListEmpty.size)

            val insertBadgeGoal = badgeDao.getBadgeByBadgeGoalID(insertBadgeGoalID)
            addAchievement(insertBadgeGoal.actionID, questID)
            val badgeDetailsList = badgeDao.getUserBadgesByUserIDAndQuestID(userID, questID).first()
            assertEquals(1, badgeDetailsList.size)
            assertEquals(insertBadgeID,badgeDetailsList[0].badgeID)
        }

    @Test
    fun testAssignAllBadgesToUser_assignsBadgeToUser() = runBlocking {
        val userID = addUser()
        val badgeID = addBadge()
        addBadgeGoal(badgeID)
        val badgeDetailsListEmpty = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(0, badgeDetailsListEmpty.size)

        badgeDao.assignAllBadgesToUser(userID)
        val badgeDetailsList = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(1, badgeDetailsList.size)
        assertEquals(badgeID, badgeDetailsList[0].badgeID)
    }

    @Test
    fun testGetBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID_returnsBadgeGoalEntity() =
        // ktlint-disable max-line-length
        runBlocking {
            val userID = addUser()
            val insertBadgeID = addBadge()
            assertNotEquals(-1, insertBadgeID)
            val insertBadgeGoalID = addBadgeGoal(insertBadgeID)
            assertNotEquals(-1, insertBadgeGoalID)
            val insertBadgeGoal = badgeDao.getBadgeByBadgeGoalID(insertBadgeGoalID)
            addStateTracking(insertBadgeGoal.actionID)
            badgeDao.assignAllBadgesToUser(userID)
            val badgeGoalEntityNull =
                badgeDao.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID)
            assertNull(badgeGoalEntityNull)

            userDao.updateWrongAnswerCountByUserID(userID, 3)
            val badgeGoalEntity =
                badgeDao.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID)
            assertNotNull(badgeGoalEntity)
            assertEquals(insertBadgeID, badgeGoalEntity?.badgeID)
        }

    @Test
    fun testGetBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID_invalidUserID_returnsNull() =
        runBlocking {
            val userID = -1
            val badgeGoalEntity =
                badgeDao.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID)
            assert(badgeGoalEntity == null)
        }

    @Test
    fun testGetCompletedGoalsForBadgeByUserID_invalidUserIDAndBadgeID_returnsEmptyList() =
        runBlocking {
            val userID = -1
            val badgeID = -1
            val actionEntityList =
                badgeDao.getCompletedGoalsForBadgeByUserID(userID, badgeID).first()
            assertEquals(0, actionEntityList.size)
        }

    @Test
    fun testGetUncompletedGoalsForBadgeByUserID_invalidUserIDAndBadgeID_returnsEmptyList() =
        runBlocking {
            val userID = -1
            val badgeID = -1
            val actionEntityList =
                badgeDao.getUncompletedGoalsForBadgeByUserID(userID, badgeID).first()
            assertEquals(0, actionEntityList.size)
        }

    private suspend fun addUser(): Int {
        val user = UserEntity(imagePath = "path/to/image.jpg", name = "Test user")
        return userDao.addUser(user).toInt()
    }

    private suspend fun addBadge(): Int {
        val badgeEntity = BadgeEntity(
            name = "Badge Name",
            description = "Badge Description",
            imagePath = "badge_image_path.jpg"
        )
        return badgeDao.addBadge(badgeEntity).toInt()
    }

    private suspend fun addBadgeGoal(badgeID: Int): Int {
        badgeGoal = BadgeGoalEntity(
            badgeID = badgeID,
            actionID = addAction()
        )

       return badgeDao.addBadgeGoal(badgeGoal).toInt()
    }

    private suspend fun addAction(): Int {
        actionEntity = ActionEntity(
            name = "Test Action",
            type = "statTracking",
            description = "Test Description",
            dialogPath = "dialogPath"
        )
        return actionDao.addAction(actionEntity).toInt()
    }

    private suspend fun addQuest(): Int {
        val quest = QuestEntity(
            name = "Test quest",
            imagePath = "path/to/image.jpg",
            dialogPath = "path/to/dialog.txt",
            description = "Test quest",
            targetGoalNumber = 1,
            longitude = 12.345,
            latitude = 67.890,
            level = 1
        )
        return questDao.addQuest(quest).toInt()
    }

    private suspend fun addStateTracking(actionID: Int): Int{
        val stateTracking = StatTrackingEntity(
            actionID = actionID,
            goal = 3,
            goalUnit = "wrongAnswerCount"
        )
        return badgeDao.addStateTracking(stateTracking).toInt()
    }

    private suspend fun addAchievement(actionID:Int, questID:Int):Int{
        val insertAchievemenID = actionDao.addAchievement(AchievementEntity(actionID,questID)).toInt()
        return insertAchievemenID
    }

    private fun isBadgeIdInList(badgeList: List<BadgeDetails>, badgeID: Int): Boolean {
        return badgeList.any { badge -> badge.badgeID == badgeID }
    }
}
