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
    @ColumnInfo(index = true) val actionID: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BadgeGoalEntity

        if (badgeID != other.badgeID) return false
        if (actionID != other.actionID) return false

        return true
    }

    override fun hashCode(): Int {
        var result = badgeID
        result = 31 * result + actionID
        return result
    }
}

