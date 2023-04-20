package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.PossibleGoalEntity

@Dao
interface PossibleGoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPossibleGoal(possibleGoal: PossibleGoalEntity)

    @Query("SELECT * FROM possibleGoal WHERE possibleGoalID = :possibleGoalID")
    fun getPossibleGoal(possibleGoalID: Int): PossibleGoalEntity?

    @Query("DELETE FROM possibleGoal WHERE possibleGoalID = :possibleGoalID")
    fun deletePossibleGoal(possibleGoalID: Int)

    @Query("SELECT * FROM possibleGoal")
    fun getAllPossibleGoals(): List<PossibleGoalEntity>
}
