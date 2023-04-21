package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goal WHERE goalID = :goalID")
    fun getGoalById(goalID: Int): Flow<GoalEntity>

    @Query("SELECT * FROM goal WHERE questID = :questID")
    fun getGoalsByQuestId(questID: Int): Flow<List<GoalEntity>>

    @Query("DELETE FROM goal WHERE goalID = :goalID")
    suspend fun deleteGoalById(goalID: Int)

    @Query("DELETE FROM goal")
    suspend fun deleteAllGoals()
}
