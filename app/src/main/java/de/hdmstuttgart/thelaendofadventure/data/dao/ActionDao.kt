package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAction(action: ActionEntity)

    @Query("SELECT * FROM action WHERE actionID = :id")
    fun getActionById(id: Int): Flow<ActionEntity?>

    @Query("SELECT * FROM action")
    fun getAllActions(): Flow<List<ActionEntity>>

    @Query("DELETE FROM action WHERE actionID = :id")
    suspend fun deleteActionById(id: Int)
}
