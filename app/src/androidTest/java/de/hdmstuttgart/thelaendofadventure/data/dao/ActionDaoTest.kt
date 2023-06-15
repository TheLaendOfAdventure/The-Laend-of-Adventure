package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import de.hdmstuttgart.thelaendofadventure.data.entity.AchievementEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActionDaoTest {

    private lateinit var actionDao: ActionDao
    private lateinit var questDao: QuestDao
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    companion object {
        private const val DIALOG_PATH = "Test Dialog Path"
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        actionDao = db.actionDao()
        questDao = db.questDao()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAddAction() = runBlocking {
        val insertedId = addAction()
        assertNotEquals(-1, insertedId)
    }

    private suspend fun addAction(): Int {
        val action = ActionEntity(
            name = "Test Action",
            type = "Test Type",
            description = "Test Description",
            dialogPath = DIALOG_PATH
        )
        return actionDao.addAction(action).toInt()
    }

    @Test
    fun testAddLocation() = runBlocking {
        val actionID = addAction()
        val location = LocationEntity(actionID, 0.0, 0.0)

        val locationID = actionDao.addLocation(location)

        assertNotEquals(-1, locationID)
    }

    @Test
    fun testAddAchievement() = runBlocking {
        val actionID = addAction()
        val questID = addQuest()
        val achievementEntity = AchievementEntity(actionID, questID)

        val achievementID = actionDao.addAchievement(achievementEntity)

        assertNotEquals(-1, achievementID)
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

    @Test
    fun testAddGoal() = runBlocking {
        val questID = addQuest()
        val actionID = addAction()
        val goal = QuestGoalEntity(questID = questID, actionID = actionID, goalNumber = 1)
        val goalID = actionDao.addGoal(goal)

        assertNotEquals(-1, goalID)
    }

    @Test
    fun getLocationByActionID_validActionID_returnsLocationEntity() = runBlocking {
        val actionID = addAction()
        val location = LocationEntity(actionID, 0.0, 0.0)
        actionDao.addLocation(location)

        val locationEntity = actionDao.getLocationByActionID(actionID)!!.first()
        assertEquals(location, locationEntity)
    }

    @Test
    fun getLocationByActionID_invalidActionID_returnsEmptyFlow() = runBlocking {
        val actionID = -1
        val locationEntity = actionDao.getLocationByActionID(actionID)?.first()

        assert(locationEntity == null)
    }

    @Test
    fun getAchievementByActionID_validActionID_returnsQuestEntity() = runBlocking {
        val actionID = addAction()
        val questID = addQuest()
        val achievementEntity = AchievementEntity(actionID, questID)
        actionDao.addAchievement(achievementEntity)

        val questEntity = actionDao.getAchievementByActionID(actionID)?.first()

        assertEquals(questID, questEntity?.questID)
    }

    @Test
    fun getAchievementByActionID_invalidActionID_returnsEmptyFlow() = runBlocking {
        val actionID = -1
        val questEntity = actionDao.getAchievementByActionID(actionID)?.first()
        assert(questEntity == null)
    }

    @Test
    fun getDialogPath_validParameters_returnsDialogPath() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val goal = QuestGoalEntity(questID = questID, actionID = actionID, goalNumber = 1)
        val goalNumber = goal.goalNumber

        questDao.assignQuestToUser(userID, questID)
        actionDao.addGoal(goal)

        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)

        assertEquals(DIALOG_PATH, dialogPath)
    }

    private suspend fun addUser(): Int {
        val user = UserEntity(imagePath = "path/to/image.jpg", name = "Test user")
        return userDao.addUser(user).toInt()
    }

    @Test
    fun getDialogPath_invalidParameters_returnsNull() = runBlocking {
        val userID = -1
        val goalNumber = -1
        val questID = -1
        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)
        assert(dialogPath == null)
    }

    @Test
    fun getDialogPath_nonExistentQuest_returnsNull() = runBlocking {
        val userID = addUser()
        val actionID = addAction()
        val goal = QuestGoalEntity(questID = addQuest(), actionID = actionID, goalNumber = 1)
        val goalNumber = goal.goalNumber
        val questID = -1

        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)

        assert(dialogPath == null)
    }

    @Test
    fun getDialogPath_invalidGoalNumber_returnsNull() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val goalNumber = -1

        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)

        assert(dialogPath == null)
    }
}
