package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE userID = :userID")
    fun getUserById(userID: Int): UserEntity

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM user WHERE userID = :userID")
    fun deleteUserById(userID: Int)

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}
