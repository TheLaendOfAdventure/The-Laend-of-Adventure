package de.hdmstuttgart.thelaendofadventure.data.offlinerepository

import de.hdmstuttgart.thelaendofadventure.data.dao.UserDao
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(private val userDao: UserDao) : UserRepository {
    companion object {
        private const val EXP_LIMIT = 100
    }

    override fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    override fun getUserById(userID: Int): Flow<UserEntity> {
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
        val user = getUserById(userID)
        user.collect {
            val currentExp = it.exp + exp
            if (currentExp < EXP_LIMIT) {
                userDao.updateExpForUserID(userID, currentExp)
            } else {
                val remainingExp = currentExp % EXP_LIMIT
                levelUpUserByID(userID, remainingExp)
            }
        }
    }

    override suspend fun updateUserWalkedKm(userID: Int, walkedKm: Int) {
        userDao.updateWalkedKmForUserID(userID, walkedKm)
    }

    private suspend fun levelUpUserByID(userID: Int, remainingExp: Int) {
        val user = getUserById(userID)

        user.collect {
            val level = it.level + 1

            updateUserLevel(userID, level)
            updateUserExp(userID, remainingExp)
        }
    }
}
