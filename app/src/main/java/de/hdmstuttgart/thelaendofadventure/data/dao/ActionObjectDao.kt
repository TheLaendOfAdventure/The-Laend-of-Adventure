package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionObjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActionObject(actionObject: ActionObjectEntity)

    @Query("SELECT * FROM actionObject WHERE ActionObjectID = :actionObjectId")
    fun getActionObjectById(actionObjectId: Int): Flow<ActionObjectEntity>

    @Query("DELETE FROM actionObject WHERE ActionObjectID = :actionObjectId")
    suspend fun deleteActionObjectById(actionObjectId: Int)

    @Query("DELETE FROM actionObject")
    suspend fun deleteAllActionObjects()
}
