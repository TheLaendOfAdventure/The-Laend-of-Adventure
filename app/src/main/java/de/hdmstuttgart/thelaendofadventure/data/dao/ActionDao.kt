package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity

@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAction(action: ActionEntity)

    @Query("SELECT * FROM action WHERE actionID = :id")
    fun getActionById(id: Int): ActionEntity?

    @Query("SELECT * FROM action")
    fun getAllActions(): List<ActionEntity>

    @Query("DELETE FROM action WHERE actionID = :id")
    fun deleteActionById(id: Int)
}
