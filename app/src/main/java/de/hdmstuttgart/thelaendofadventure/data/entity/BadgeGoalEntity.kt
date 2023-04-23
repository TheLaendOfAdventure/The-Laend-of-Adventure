package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "badgeGoal",
    foreignKeys = [
        ForeignKey(
            entity = BadgeEntity::class,
            childColumns = ["badgeID"],
            parentColumns = ["badgeID"]
        ),
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class BadgeGoalEntity(
    @PrimaryKey(autoGenerate = true) val badgeGoalID: Int = 0,
    @ColumnInfo(index = true) val badgeID: Int,
    @ColumnInfo(index = true) val actionID: Int,
    val goalNumber: Int
)
