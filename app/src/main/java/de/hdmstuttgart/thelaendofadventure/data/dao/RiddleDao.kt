package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.RiddleEntity

@Dao
interface RiddleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRiddle(riddle: RiddleEntity)

    @Query("SELECT * FROM riddle WHERE riddleID = :riddleID")
    fun getRiddleById(riddleID: Int): RiddleEntity

    @Query("DELETE FROM riddle")
    fun deleteAllRiddles()
}
