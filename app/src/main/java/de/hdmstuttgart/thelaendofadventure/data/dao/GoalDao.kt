package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.GoalEntity

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goal WHERE goalID = :goalID")
    fun getGoalById(goalID: Int): GoalEntity

    @Query("SELECT * FROM goal WHERE questID = :questID")
    fun getGoalsByQuestId(questID: Int): List<GoalEntity>

    @Query("DELETE FROM goal WHERE goalID = :goalID")
    fun deleteGoalById(goalID: Int)

    @Query("DELETE FROM goal")
    fun deleteAllGoals()
}
