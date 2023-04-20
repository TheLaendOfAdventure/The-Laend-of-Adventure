package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertQuest(quest: QuestEntity)

    @Query("SELECT * FROM quest WHERE questID = :questID")
    fun getQuestById(questID: Int): QuestEntity

    @Query("SELECT * FROM quest WHERE userID = :userID")
    fun getQuestsByUser(userID: String): List<QuestEntity>

    @Query("DELETE FROM quest WHERE questID = :questID")
    fun deleteQuestById(questID: Int)

    @Query("DELETE FROM quest")
    fun deleteAllQuests()
}
