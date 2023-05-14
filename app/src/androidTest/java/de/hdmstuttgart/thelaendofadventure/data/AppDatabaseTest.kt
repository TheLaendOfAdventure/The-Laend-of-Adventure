package de.hdmstuttgart.thelaendofadventure.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hdmstuttgart.thelaendofadventure.data.dao.UserDao
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
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

    @Test
    fun levelUpUserByUserID() = runBlocking {
        val user = UserEntity(1, "TestUser1", "ImagePath1", 1, 1, 90)
        userDao.addUser(user)
        userDao.updateExpForUserID(user.userID, 20)
        assertEquals(2, user.level)
        assertEquals(10, user.exp)
        val user2 = UserEntity(2, "TestUser2", "ImagePath2", 2, 2, 60)
        userDao.addUser(user2)
        userDao.updateExpForUserID(user2.userID, 20)
        assertEquals(2, user2.level)
        assertEquals(80, user2.exp)
    }

    @Test(expected = NoSuchElementException::class)
    fun testUserNotInDatabase() = runBlocking {
        userDao.updateExpForUserID(999, 20)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNegativeExp() = runBlocking {
        val user = UserEntity(3, "TestUser3", "ImagePath3", 3, 3, 90)
        userDao.addUser(user)
        userDao.updateExpForUserID(user.userID, -20)
    }
}
