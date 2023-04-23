package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.PossibleGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PossibleGoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPossibleGoal(possibleGoal: PossibleGoalEntity)

    @Query("SELECT * FROM possibleGoal WHERE possibleGoalID = :possibleGoalID")
    fun getPossibleGoal(possibleGoalID: Int): Flow<PossibleGoalEntity?>

    @Query("DELETE FROM possibleGoal WHERE possibleGoalID = :possibleGoalID")
    suspend fun deletePossibleGoal(possibleGoalID: Int)

    @Query("SELECT * FROM possibleGoal")
    fun getAllPossibleGoals(): Flow<List<PossibleGoalEntity>>
}
