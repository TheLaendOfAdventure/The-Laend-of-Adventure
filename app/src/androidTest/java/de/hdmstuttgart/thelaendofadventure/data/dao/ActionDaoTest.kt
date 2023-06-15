package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActionDaoTest {

    private lateinit var actionDao: ActionDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        actionDao = db.actionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getLocationByActionID_validActionID_returnsLocationEntity() = runBlocking {
        val actionID = 7
        val locationEntity = actionDao.getLocationByActionID(actionID).first()
        assert(locationEntity != null)
    }

    @Test
    fun getLocationByActionID_invalidActionID_returnsEmptyFlow() = runBlocking {
        val actionID = -1
        val locationEntity = actionDao.getLocationByActionID(actionID).first()
        assert(locationEntity == null)
    }

    @Test
    fun getAchievementByActionID_validActionID_returnsQuestEntity() = runBlocking {
        val actionID = 9
        val questEntity = actionDao.getAchievementByActionID(actionID).first()
        assert(questEntity != null)
    }

    @Test
    fun getAchievementByActionID_invalidActionID_returnsEmptyFlow() = runBlocking {
        val actionID = -1
        val questEntity = actionDao.getAchievementByActionID(actionID).first()
        assert(questEntity == null)
    }

    @Test
    fun getRiddleAndAnswersByActionID_validActionID_returnsRiddleAndAnswersMap() = runBlocking {
        val actionID = 8
        val riddleAndAnswersMap = actionDao.getRiddleAndAnswersByActionID(actionID).first()
        assert(riddleAndAnswersMap.isNotEmpty())
    }

    @Test
    fun getRiddleAndAnswersByActionID_invalidActionID_returnsEmptyFlow() = runBlocking {
        val actionID = -1
        val riddleAndAnswersMap = actionDao.getRiddleAndAnswersByActionID(actionID).first()
        assert(riddleAndAnswersMap.isEmpty())
    }

    @Test
    fun getDialogPath_validParameters_returnsDialogPath() = runBlocking {
        val userID = 1
        val goalNumber = 1
        val questID = 1
        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)
        assert(dialogPath != null)
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
        val userID = 1
        val goalNumber = 1
        val questID = 9999
        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)
        assert(dialogPath == null)
    }

    @Test
    fun getDialogPath_invalidGoalNumber_returnsNull() = runBlocking {
        val userID = 1
        val goalNumber = -1
        val questID = 1
        val dialogPath = actionDao.getDialogPath(userID, goalNumber, questID)
        assert(dialogPath == null)
    }
}
