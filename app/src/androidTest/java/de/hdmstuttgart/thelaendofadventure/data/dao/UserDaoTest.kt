package de.hdmstuttgart.thelaendofadventure.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import de.hdmstuttgart.thelaendofadventure.data.AppDatabase
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private suspend fun addUser(): Int {
        val user = UserEntity(name = "John Doe", imagePath = "image_path", level = 1, walkedKm = 1)
        return userDao.addUser(user).toInt()
    }

    @Test
    fun testAddUser() = runBlocking {
        val userId = addUser()
        assertEquals(1, userId)
    }

    @Test
    fun testGetUserById() = runBlocking {
        val user = UserEntity(1, "John Doe", "image_path", 1, 1)
        userDao.addUser(user)
        val retrievedUser = userDao.getUserById(1).first()
        assertEquals(user, retrievedUser)
    }

    @Test
    fun testGetLevelByUserID() = runBlocking {
        addUser()
        val level = userDao.getLevelByUserID(1).first()
        assertEquals(1, level)
    }

    @Test
    fun testUpdateNameForUserID() = runBlocking {
        addUser()
        userDao.updateNameForUserID(1, "Jane Doe")
        val updatedUser = userDao.getUserById(1).first()
        assertEquals("Jane Doe", updatedUser.name)
    }

    @Test
    fun testUpdateImagePathForUserID() = runBlocking {
        addUser()
        userDao.updateImagePathForUserID(1, "new_image_path")
        val updatedUser = userDao.getUserById(1).first()
        assertEquals("new_image_path", updatedUser.imagePath)
    }

    @Test
    fun testUpdateLevelForUserID() = runBlocking {
        addUser()
        userDao.updateLevelForUserID(1, 2)
        val updatedLevel = userDao.getLevelByUserID(1).first()
        assertEquals(2, updatedLevel)
    }

    @Test
    fun testUpdateExpForUserID() = runBlocking {
        addUser()
        userDao.updateExpForUserID(1, 50)
        val updatedUser = userDao.getUserById(1).first()
        assertEquals(50, updatedUser.exp)
    }

    @Test
    fun testGetWrongAnswerCountByUserID() = runBlocking {
        addUser()
        val wrongAnswerCount = userDao.getWrongAnswerCountByUserID(1)
        assertEquals(0, wrongAnswerCount)
    }

    @Test
    fun testUpdateWrongAnswerCountByUserID() = runBlocking {
        addUser()
        userDao.updateWrongAnswerCountByUserID(1, 5)
        val updatedWrongAnswerCount = userDao.getWrongAnswerCountByUserID(1)
        assertEquals(5, updatedWrongAnswerCount)
    }

    @Test()
    fun testGetLevelByUserID_InvalidId() = runBlocking {
        addUser()
        val level = userDao.getLevelByUserID(-1).first()
        assertEquals(0, level)
    }

    @Test(expected = java.lang.NullPointerException::class)
    fun testUpdateImagePathForUserID_InvalidId() = runBlocking {
        addUser()
        userDao.updateImagePathForUserID(-1, "new_image_path")
        val updatedUser = userDao.getUserById(-1).first()
        assertEquals("new_image_path", updatedUser.userID)
    }

    @Test(expected = java.lang.NullPointerException::class)
    fun testUpdateLevelForUserID_InvalidId() = runBlocking {
        addUser()
        userDao.updateLevelForUserID(-1, 3)
        val updatedUser = userDao.getUserById(-1).first()
        assertEquals(3, updatedUser.level)
    }

    @Test(expected = java.lang.NullPointerException::class)
    fun testUpdateExpForUserID_InvalidId() = runBlocking {
        addUser()
        userDao.updateExpForUserID(-1, 50)
        val updatedUser = userDao.getUserById(-1).first()
        assertEquals(50, updatedUser.exp)
    }

    @Test
    fun testGetWrongAnswerCountByUserID_InvalidId() = runBlocking {
        addUser()
        val wrongAnswerCount = userDao.getWrongAnswerCountByUserID(-1)
        assertEquals(0, wrongAnswerCount)
    }

    @Test(expected = java.lang.NullPointerException::class)
    fun testUpdateWrongAnswerCountByUserID_InvalidId() = runBlocking {
        addUser()
        userDao.updateWrongAnswerCountByUserID(-1, 5)
        val updatedUser = userDao.getUserById(-1).first()
        assertEquals(5, updatedUser.wrongAnswerCount)
    }
}
