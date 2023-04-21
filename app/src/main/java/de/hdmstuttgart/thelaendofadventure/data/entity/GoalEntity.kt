package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal",
    foreignKeys = [
        ForeignKey(
            entity = QuestEntity::class,
            childColumns = ["questID"],
            parentColumns = ["questID"]
        ),
        ForeignKey(
            entity = PossibleGoalEntity::class,
            childColumns = ["possibleGoalID"],
            parentColumns = ["possibleGoalID"]
        )
    ]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val goalID: Int = 0,
    @ColumnInfo(name = "questID", index = true) val questID: Int,
    @ColumnInfo(name = "possibleGoalID") val possibleGoalID: Int
)
