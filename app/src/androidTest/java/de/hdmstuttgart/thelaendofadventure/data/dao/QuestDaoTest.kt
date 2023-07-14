package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import de.hdmstuttgart.thelaendofadventure.data.entity.AchievementEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleAnswersEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestDaoTest {

    private lateinit var badgeDao: BadgeDao
    private lateinit var actionDao: ActionDao
    private lateinit var questDao: QuestDao
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    companion object {
        private val QUEST = QuestEntity(
            name = "Test quest",
            imagePath = "path/to/image.jpg",
            dialogPath = "path/to/dialog",
            description = "Test quest",
            targetGoalNumber = 10,
            longitude = 12.345,
            latitude = 67.890,
            level = 1
        )
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        actionDao = db.actionDao()
        questDao = db.questDao()
        userDao = db.userDao()
        badgeDao = db.badgeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAddQuest() = runBlocking {
        val quest = QuestEntity(
            name = "Meine Quest",
            imagePath = "path/zum/bild.jpg",
            dialogPath = "path/zum/dialog",
            description = "Beschreibung der Quest",
            targetGoalNumber = 10,
            longitude = 123.456,
            latitude = 78.9,
            level = 5
        )
        val insertedId = questDao.addQuest(quest)
        assertNotEquals(-1, insertedId)

        val insertedQuest = questDao.getQuestByQuestID(insertedId.toInt())
        assertNotNull(insertedQuest)
        assertEquals(quest, insertedQuest)
        assertEquals(quest.name, insertedQuest.name)
        assertEquals(quest.targetGoalNumber, insertedQuest.targetGoalNumber)
        assertEquals(quest.description, insertedQuest.description)
    }

    @Test
    fun testGetDialogPathByQuestID() = runBlocking {
        val questID = addQuest()
        val dialogPath = questDao.getDialogPathByQuestID(questID)
        assertNotNull(dialogPath)
        assertEquals("path/to/dialog", dialogPath)
    }

    @Test
    fun testGetQuestByQuestID() = runBlocking {
        val internalQuest = QuestEntity(
            name = "Meine Quest",
            imagePath = "path/zum/bild.jpg",
            dialogPath = "path/zum/dialog",
            description = "Beschreibung der Quest",
            targetGoalNumber = 10,
            longitude = 123.456,
            latitude = 78.9,
            level = 5
        )
        val insertedQuestID = questDao.addQuest(internalQuest).toInt()
        val quest = questDao.getQuestByQuestID(insertedQuestID)
        assertNotNull(quest)
        assertEquals(internalQuest, quest)
    }

    @Test
    fun testGetAcceptedQuestsByUserID() = runBlocking {
        val userID = addUser()
        val quest = addQuest()

        val quests = questDao.getAcceptedQuestsByUserID(userID).first()
        assertTrue(quests.isEmpty())

        questDao.assignQuestToUser(userID, quest)
        val assignedQuests = questDao.getAcceptedQuestsByUserID(userID).first()
        assertEquals(QUEST, assignedQuests[0])
    }

    @Test
    fun testGetUnacceptedQuestsByUserID() = runBlocking {
        val userID = addUser()
        val quests = questDao.getUnacceptedQuestsByUserID(userID).first()
        assertTrue(quests.isEmpty())

        addQuest()
        val assignedQuests = questDao.getUnacceptedQuestsByUserID(userID).first()
        assertEquals(QUEST, assignedQuests[0])
    }

    @Test
    fun testGetProgressForQuestByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        questDao.assignQuestToUser(userID, questID)

        val progress = questDao.getProgressForQuestByUserID(userID, questID).first()
        assertNotNull(progress)
        assertEquals(0, progress.currentGoalNumber)

        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedProgress = questDao.getProgressForQuestByUserID(userID, questID).first()
        assertNotNull(updatedProgress)
        assertEquals(1, updatedProgress.currentGoalNumber)
    }

    @Test
    fun testGetQuestsWithDetailsByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        questDao.assignQuestToUser(userID, questID)

        val quests = questDao.getQuestsWithDetailsByUserID(userID).first()
        assertNotNull(quests)
        assertTrue(quests.isNotEmpty())

        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedQuests = questDao.getQuestsWithDetailsByUserID(userID).first()
        assertNotNull(updatedQuests)
        assertNotEquals(quests, updatedQuests)
        assertTrue(updatedQuests.any { it.questID == questID && it.currentGoalNumber == 1 })
    }

    @Test
    fun testGetCompletedGoalsForQuestByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val goalID = addQuestGoal(questID, actionID, 1)
        questDao.assignQuestToUser(userID, questID)

        val completedGoals = questDao.getCompletedGoalsForQuestByUserID(userID, questID).first()
        assertNotNull(completedGoals)
        assertTrue(completedGoals.isEmpty())

        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedCompletedGoals =
            questDao.getCompletedGoalsForQuestByUserID(userID, questID).first()
        assertNotNull(updatedCompletedGoals)
        assertNotEquals(completedGoals, updatedCompletedGoals)
        assertTrue(updatedCompletedGoals.isNotEmpty())
        assertTrue(updatedCompletedGoals.any { it.actionID == goalID })
    }

    @Test
    fun testGetUncompletedGoalsForQuestByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val goalID = addQuestGoal(questID, actionID, 1)
        questDao.assignQuestToUser(userID, questID)

        val uncompletedGoals = questDao.getUncompletedGoalsForQuestByUserID(userID, questID).first()
        assertTrue(uncompletedGoals.isNotEmpty())
        assertTrue(uncompletedGoals.any { it.actionID == goalID })

        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedUncompletedGoals =
            questDao.getUncompletedGoalsForQuestByUserID(userID, questID).first()
        assertTrue(updatedUncompletedGoals.all { it.actionID != goalID })
        assertTrue(updatedUncompletedGoals.isEmpty())
    }

    @Test
    fun testUpdateQuestProgressByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        questDao.assignQuestToUser(userID, questID)

        val progress = questDao.getProgressForQuestByUserID(userID, questID).first()
        assertNotNull(progress)
        assertEquals(0, progress.currentGoalNumber)

        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedProgress = questDao.getProgressForQuestByUserID(userID, questID).first()
        assertNotNull(updatedProgress)
        assertEquals(1, updatedProgress.currentGoalNumber)
    }

    @Test
    fun testAssignQuestToUser() = runBlocking {
        val userID = addUser()
        val questID = addQuest()

        val quests = questDao.getAcceptedQuestsByUserID(userID).first()
        assertNotNull(quests)
        assertTrue(quests.all { it.questID != questID })

        questDao.assignQuestToUser(userID, questID)

        val updatedQuests = questDao.getAcceptedQuestsByUserID(userID).first()
        assertNotNull(updatedQuests)
        assertTrue(updatedQuests.any { it.questID == questID })
    }

    @Test
    fun testGetLocationForAcceptedQuestsByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val location = addLocation(actionID)

        questDao.assignQuestToUser(userID, questID)

        val locationGoals = questDao.getLocationForAcceptedQuestsByUserID(userID).first()
        assertTrue(locationGoals.isEmpty())
        assertTrue(
            locationGoals.all {
                it.longitude != location.longitude && it.latitude != location.latitude
            }
        )

        addQuestGoal(questID, actionID, 1)
        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedLocationGoals = questDao.getLocationForAcceptedQuestsByUserID(userID).first()

        assertTrue(updatedLocationGoals.isNotEmpty())
        assertTrue(
            updatedLocationGoals.any {
                it.longitude == location.longitude && it.latitude == location.latitude
            }
        )
    }

    @Test
    fun testGetRiddleForAcceptedQuestsByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val riddle = addRiddle(actionID)

        questDao.assignQuestToUser(userID, questID)

        val riddleDetails = questDao.getRiddleForAcceptedQuestsByUserID(userID).first()
        assertTrue(riddleDetails.isEmpty())
        assertTrue(
            riddleDetails.all {
                it.question != riddle.question && it.answer != riddle.answer
            }
        )

        addQuestGoal(questID, actionID, 1)
        questDao.updateQuestProgressByUserID(userID, questID, 1)
        val updatedRiddleDetails = questDao.getRiddleForAcceptedQuestsByUserID(userID).first()

        assertTrue(updatedRiddleDetails.isNotEmpty())
        assertTrue(
            updatedRiddleDetails.any {
                it.question == riddle.question && it.answer == riddle.answer
            }
        )
    }

    @Test
    fun testGetQuestForBadgeByUserID() = runBlocking {
        val userID = addUser()
        val questID = addQuest()
        val actionID = addAction()
        val badgeID = addBadge()

        addAchievement(questID, actionID)
        addQuestGoal(questID, actionID, 1)
        badgeDao.assignAllBadgesToUser(userID)

        val quests = questDao.getQuestForBadgeByUserID(userID, badgeID).first()
        assertTrue(quests.isEmpty())
        assertTrue(quests.all { it != questID })

        addBadgeGoal(badgeID, actionID)
        val updatedQuests = questDao.getQuestForBadgeByUserID(userID, badgeID).first()
        assertTrue(updatedQuests.isNotEmpty())
        assertTrue(updatedQuests.any { it == questID })
    }

    @Test
    fun testGetAllActionDescriptionsByQuestID() = runBlocking {
        val questID = 1
        val actionDescriptions = questDao.getAllActionDescriptionsByQuestID(questID).first()
        assertNotNull(actionDescriptions)
        // Weitere Assertions entsprechend den erwarteten Daten
    }

    @Test
    fun testGetQuestImageByQuestID() = runBlocking {
        val questID = 1
        val imagePath = questDao.getQuestImageByQuestID(questID)
        assertNotNull(imagePath)
        assertEquals("path/to/image", imagePath)
    }

    @Test
    fun testGetNameByQuestByGoal() = runBlocking {
        val questID = 1
        val goalNumber = 1
        val name = questDao.getNameByQuestByGoal(questID, goalNumber)
        assertNotNull(name)
        assertEquals("Action Name", name)
    }

    @Test
    fun testGetLocationByQuestByGoal() = runBlocking {
        val questID = 1
        val goalNumber = 1
        val location = questDao.getLocationByQuestByGoal(questID, goalNumber)
        assertNotNull(location)
        // Weitere Assertions entsprechend den erwarteten Daten
    }

    @Test
    fun testGetOnlyLocationForAcceptedQuestsByUserID() = runBlocking {
        val userID = 1
        val locations = questDao.getOnlyLocationForAcceptedQuestsByUserID(userID)
        assertNotNull(locations)
        // Weitere Assertions entsprechend den erwarteten Daten
    }

    // Negativtests
    @Test
    fun testAddQuest_Negative() = runBlocking {
        val quest = QuestEntity(
            name = "Meine Quest",
            imagePath = "path/zum/bild.jpg",
            dialogPath = "path/zum/dialog.txt",
            description = "Beschreibung der Quest",
            targetGoalNumber = 10,
            longitude = 123.456,
            latitude = 78.9,
            level = 5
        )
        val insertedId = questDao.addQuest(quest)
        assertEquals(-1, insertedId)
    }

    @Test
    fun testGetDialogPathByQuestID_Negative() = runBlocking {
        val questID = -1
        val dialogPath = questDao.getDialogPathByQuestID(questID)
        assertNull(dialogPath)
    }

    @Test
    fun testGetQuestByQuestID_Negative() = runBlocking {
        val questID = -1
        val quest = questDao.getQuestByQuestID(questID)
        assertNull(quest)
    }

    @Test
    fun testGetAcceptedQuestsByUserID_Negative() = runBlocking {
        val userID = -1
        val quests = questDao.getAcceptedQuestsByUserID(userID).first()
        assertNotNull(quests)
        assertTrue(quests.isEmpty())
    }

    @Test
    fun testGetUnacceptedQuestsByUserID_Negative() = runBlocking {
        val userID = -1
        val quests = questDao.getUnacceptedQuestsByUserID(userID).first()
        assertNotNull(quests)
        assertTrue(quests.isEmpty())
    }

    private suspend fun addQuest(): Int {
        return questDao.addQuest(QUEST).toInt()
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

    private suspend fun addQuestGoal(questID: Int, actionID: Int, goalNumber: Int): Int {
        val questGoal = QuestGoalEntity(
            questID = questID,
            actionID = actionID,
            goalNumber = goalNumber
        )

        return questDao.addQuestGoal(questGoal).toInt()
    }

    private suspend fun addBadgeGoal(badgeID: Int, actionID: Int): Int {
        val badgeGoal = BadgeGoalEntity(
            badgeID = badgeID,
            actionID = actionID
        )
        return badgeDao.addBadgeGoal(badgeGoal).toInt()
    }

    private suspend fun addLocation(actionID: Int): LocationEntity {
        val location = LocationEntity(
            actionID = actionID,
            latitude = 123.456,
            longitude = 78.9
        )
        actionDao.addLocation(location)

        return location
    }

    private suspend fun addRiddle(actionID: Int): RiddleEntity {
        val riddle = RiddleEntity(
            actionID = actionID,
            question = "Was ist schwerer: ein Kilogramm Federn oder ein Kilogramm Stahl?",
            answer = "Beide wiegen gleich",
            hint = "Denk an die Einheit der Masse"
        )

        val riddleAnswer = RiddleAnswersEntity(
            actionID = actionID,
            answer = "Beide wiegen gleich"
        )

        actionDao.addRiddle(riddle)
        actionDao.addRiddleAnswer(riddleAnswer)

        return riddle
    }

    private suspend fun addBadge(): Int {
        val badgeEntity = BadgeEntity(
            name = "Badge Name",
            description = "Badge Description",
            imagePath = "badge_image_path.jpg"
        )
        return badgeDao.addBadge(badgeEntity).toInt()
    }

    private suspend fun addAchievement(questID: Int, actionID: Int): AchievementEntity {
        val achievement = AchievementEntity(
            actionID = actionID,
            questID = questID
        )

        actionDao.addAchievement(achievement)
        return achievement
    }

    private suspend fun addUser(): Int {
        val user = UserEntity(imagePath = "path/to/image.jpg", name = "Test user")
        return userDao.addUser(user).toInt()
    }
}
