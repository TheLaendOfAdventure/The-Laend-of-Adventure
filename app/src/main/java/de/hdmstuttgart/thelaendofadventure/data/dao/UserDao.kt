package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    fun addUser(user: UserEntity): Long

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE userID = :id")
    fun getUserById(id: Int): Flow<UserEntity>

    @Query(
        "UPDATE user SET name = :name " +
            "WHERE userID = :userID;"
    )
    suspend fun updateNameForUserID(userID: Int, name: String)

    @Query(
        "UPDATE user SET imagePath = :imagePath " +
            "WHERE userID = :userID;"
    )
    suspend fun updateImagePathForUserID(userID: Int, imagePath: String)

    @Query(
        "UPDATE user SET level = :level " +
            "WHERE userID = :userID"
    )
    suspend fun updateLevelForUserID(userID: Int, level: Int)

    @Query(
        "UPDATE user SET exp = :exp " +
            "WHERE userID = :userID"
    )
    suspend fun updateExpForUserID(userID: Int, exp: Int)

    @Query(
        "UPDATE user SET walkedKm = :walkedKm " +
            "WHERE userID = :userID"
    )
    suspend fun updateWalkedKmForUserID(userID: Int, walkedKm: Int)
}
