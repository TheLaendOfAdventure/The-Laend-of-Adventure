package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "possibleGoal",
    foreignKeys = [
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        ),
        ForeignKey(
            entity = ActionObjectEntity::class,
            childColumns = ["actionObjectID"],
            parentColumns = ["actionObjectID"]
        )
    ]
)
data class PossibleGoalEntity(
    @PrimaryKey(autoGenerate = true) val possibleGoalID: Int = 0,
    @ColumnInfo(name = "actionID", index = true) val actionID: Int,
    @ColumnInfo(name = "actionObjectID", index = true) val actionObjectID: Int
)
