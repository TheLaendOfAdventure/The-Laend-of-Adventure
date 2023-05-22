package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_quest",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            childColumns = ["userID"],
            parentColumns = ["userID"]
        ),
        ForeignKey(
            entity = QuestEntity::class,
            childColumns = ["questID"],
            parentColumns = ["questID"]
        )
    ]
)
data class UserQuestEntity(
    @PrimaryKey(autoGenerate = true) val userQuestID: Int = 0,
    @ColumnInfo(index = true) val userID: Int,
    @ColumnInfo(index = true) val questID: Int,
    val currentGoalNumber: Int = 1
)
