package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.UserDao
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(private val userDao: UserDao) : UserRepository {

    override fun addUser(user: UserEntity) {
        userDao.addUser(user)
    }

    override fun getAllUsers(): Flow<UserEntity> {
        return userDao.getAllUsers()
    }

    override fun getUserByID(userID: Int): Flow<UserEntity> {
        return userDao.getUserById(userID)
    }

    override suspend fun updateUserName(userID: Int, name: String) {
        userDao.updateNameForUserID(userID, name)
    }

    override suspend fun updateUserImagePath(userID: Int, imagePath: String) {
        userDao.updateImagePathForUserID(userID, imagePath)
    }

    override suspend fun updateUserLevel(userID: Int, level: Int) {
        userDao.updateLevelForUserID(userID, level)
    }

    override suspend fun updateUserExp(userID: Int, exp: Int) {
        userDao.updateExpForUserID(userID, exp)
    }

    override suspend fun updateUserWalkedKm(userID: Int, walkedKm: Int) {
        userDao.updateWalkedKmForUserID(userID, walkedKm)
    }
}
