package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
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

    private fun isBadgeIdInList(badgeList: List<BadgeDetails>, badgeID: Int): Boolean {
        return badgeList.any { badge -> badge.badgeID == badgeID }
    }

    @Test
    fun testDeleteAllBadges() = runBlocking {
        val userID = addUser()

        addBadge()
        addBadge()
        badgeDao.assignAllBadgesToUser(userID)

        val initialBadgeCount = badgeDao.getBadgesDetailsByUserID(userID).first().size
        assertEquals(2, initialBadgeCount)

        badgeDao.deleteAllBadges()

        // Überprüfen, ob alle Badges gelöscht wurden
        val finalBadgeCount = badgeDao.getBadgesDetailsByUserID(userID).first().size
        assertEquals(0, finalBadgeCount)
    }

    @Test
    fun testGetCompletedBadgesDetailsByUserID_validUserID_returnsBadgeDetailsList() = runBlocking {
        val userID = addUser()
        val badgeDetailsList = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(0, badgeDetailsList.size)
        // Assuming there are no badges assigned to the user initially
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
        assert(badgeEntity == null)
    }

    @Test
    fun testGetUnCompletedBadgesDetailsByUserID_validUserID_returnsBadgeDetailsList() =
        runBlocking {
            val userID = addUser()
            val badgeDetailsList = badgeDao.getBadgesDetailsByUserID(userID).first()
            assertEquals(
                0,
                badgeDetailsList.size
            ) // Assuming there are no unaccepted badges for the user initially
        }

    @Test
    fun testGetCompletedGoalsForBadgeByUserID_validUserIDAndBadgeID_returnsActionEntityList() =
        runBlocking {
            val userID = addUser()
            val badgeID = addBadge()
            val actionEntityList =
                badgeDao.getCompletedGoalsForBadgeByUserID(userID, badgeID).first()
            assertEquals(0, actionEntityList.size)
        }

    @Test
    fun testGetUncompletedGoalsForBadgeByUserID_validUserIDAndBadgeID_returnsActionEntityList() =
        runBlocking {
            val userID = addUser()
            val badgeID = addBadge()
            val actionEntityList =
                badgeDao.getUncompletedGoalsForBadgeByUserID(userID, badgeID).first()
            assertEquals(0, actionEntityList.size)
        }

    @Test
    fun testGetUserBadgesByUserIDAndQuestID_validUserIDAndQuestID_returnsBadgeDetailsList() =
        runBlocking {
            val userID = addUser()
            val questID = addQuest()
            val badgeDetailsList = badgeDao.getUserBadgesByUserIDAndQuestID(userID, questID).first()
            assertEquals(0, badgeDetailsList.size)
        }

    @Test
    fun testAssignAllBadgesToUser_validUserIDAndBadgeID_assignsBadgeToUser() = runBlocking {
        val userID = addUser()
        val badgeID = addBadge()
        badgeDao.assignAllBadgesToUser(userID)
        val badgeDetailsList = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(1, badgeDetailsList.size)
        assertEquals(badgeID, badgeDetailsList[0].badgeID)
    }

    @Test
    fun testGetBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID_validUserID_returnsBadgeGoalEntity() =
        // ktlint-disable max-line-length
        runBlocking {
            val userID = addUser()
            val badgeGoalEntity =
                badgeDao.getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID)
            assertNotEquals(null, badgeGoalEntity)
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

    @Test
    fun testAssignAllBadgesToUser_invalidUserID_doesNotAssignBadgeToUser() = runBlocking {
        val userID = -1
        addBadge()
        badgeDao.assignAllBadgesToUser(userID)
        val badgeDetailsList = badgeDao.getBadgesDetailsByUserID(userID).first()
        assertEquals(0, badgeDetailsList.size)
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

    private suspend fun addBadgeGoal(badgeID: Int) {
        val badgeGoal = BadgeGoalEntity(
            badgeID = badgeID,
            actionID = addAction()
        )

        badgeDao.addBadgeGoal(badgeGoal)
    }

    private suspend fun addAction(): Int {
        val action = ActionEntity(
            name = "Test Action",
            type = "Test Type",
            description = "Test Description",
            dialogPath = "dialogPath"
        )
        return actionDao.addAction(action).toInt()
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
}
