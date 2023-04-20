package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionObjectEntity

@Dao
interface ActionObjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertActionObject(actionObject: ActionObjectEntity)

    @Query("SELECT * FROM actionObject WHERE ActionObjectID = :actionObjectId")
    fun getActionObjectById(actionObjectId: Int): ActionObjectEntity

    @Query("DELETE FROM actionObject WHERE ActionObjectID = :actionObjectId")
    fun deleteActionObjectById(actionObjectId: Int)

    @Query("DELETE FROM actionObject")
    fun deleteAllActionObjects()
}
