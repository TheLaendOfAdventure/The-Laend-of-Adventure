package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "questGoal",
    foreignKeys = [
        ForeignKey(
            entity = QuestEntity::class,
            childColumns = ["questID"],
            parentColumns = ["questID"]
        ),
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class QuestGoalEntity(
    @PrimaryKey(autoGenerate = true) val questGoalID: Int = 0,
    @ColumnInfo(index = true) val questID: Int,
    @ColumnInfo(index = true) val actionID: Int,
    val goalNumber: Int
)
