package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiddleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRiddle(riddle: RiddleEntity)

    @Query("SELECT * FROM riddle WHERE riddleID = :riddleID")
    fun getRiddleById(riddleID: Int): Flow<RiddleEntity>

    @Query("DELETE FROM riddle")
    suspend fun deleteAllRiddles()
}
