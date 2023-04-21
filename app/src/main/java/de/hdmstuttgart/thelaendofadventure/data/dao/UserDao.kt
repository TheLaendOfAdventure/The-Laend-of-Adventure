package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE userID = :userID")
    fun getUserById(userID: Int): Flow<UserEntity>

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM user WHERE userID = :userID")
    suspend fun deleteUserById(userID: Int)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}
