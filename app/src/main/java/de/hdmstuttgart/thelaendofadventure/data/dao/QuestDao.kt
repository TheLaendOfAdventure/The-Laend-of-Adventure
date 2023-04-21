package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuest(quest: QuestEntity)

    @Query("SELECT * FROM quest WHERE questID = :questID")
    fun getQuestById(questID: Int): Flow<QuestEntity>

    @Query("SELECT * FROM quest WHERE userID = :userID")
    fun getQuestsByUser(userID: String): Flow<List<QuestEntity>>

    @Query("DELETE FROM quest WHERE questID = :questID")
    suspend fun deleteQuestById(questID: Int)

    @Query("DELETE FROM quest")
    suspend fun deleteAllQuests()
}
