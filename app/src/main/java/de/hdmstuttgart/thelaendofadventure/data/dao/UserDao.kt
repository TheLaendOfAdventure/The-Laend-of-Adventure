package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeGoalEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.StatTracking
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    fun addUser(user: UserEntity): Long

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE userID = :id")
    fun getUserById(id: Int): Flow<UserEntity>

    @Query("SELECT level FROM user WHERE userID = :id")
    fun getLevelByUserID(id: Int): Flow<Int>

    @Query(
        "UPDATE user SET name = :name " +
            "WHERE userID = :userID;"
    )
    suspend fun updateNameForUserID(userID: Int, name: String)

    @Query(
        "UPDATE user SET imagePath = :imagePath " +
            "WHERE userID = :userID;"
    )
    suspend fun updateImagePathForUserID(userID: Int, imagePath: String)

    @Query(
        "UPDATE user SET level = :level " +
            "WHERE userID = :userID"
    )
    suspend fun updateLevelForUserID(userID: Int, level: Int)

    @Query(
        "UPDATE user SET exp = :exp " +
            "WHERE userID = :userID"
    )
    suspend fun updateExpForUserID(userID: Int, exp: Int)

    @Query(
        "UPDATE user SET walkedKm = :walkedKm " +
            "WHERE userID = :userID"
    )
    suspend fun updateWalkedKmForUserID(userID: Int, walkedKm: Int)

    @Query(
        "SELECT wrongRiddleAnswers FROM user " +
            "WHERE userID = :userID"
    )
    suspend fun getWrongRiddleAnswersByUserID(userID: Int): Int

    @Query(
        "UPDATE user SET wrongRiddleAnswers = :wrongRiddleAnswers " +
            "WHERE userID = :userID"
    )
    suspend fun updateWrongRiddleAnswersByUserID(userID: Int, wrongRiddleAnswers: Int)

    @Query(
        "SELECT badgeGoal.* " +
            "FROM badgeGoal " +
            "JOIN action ON badgeGoal.actionID = action.actionID " +
            "JOIN user_badge ON badgeGoal.badgeID = user_badge.badgeID " +
            "JOIN user ON user_badge.userID = user.userID " +
            "JOIN statTracking ON action.actionID = statTracking.actionID " +
            "WHERE user.userID = :userID " +
            "AND statTracking.goal = user.wrongRiddleAnswers " +
            "AND statTracking.goalUnit = 'wrongRiddleAnswers'"
    )
    suspend fun getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID(userID: Int): BadgeGoalEntity

    @Query(
        "SELECT * " +
            "FROM statTracking "
    )
    suspend fun getBadgeGoalWhenWrongRiddleAnswersIsReachedByUserID2(): StatTracking
}
