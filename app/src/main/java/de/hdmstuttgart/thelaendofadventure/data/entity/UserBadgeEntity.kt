package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_badge",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            childColumns = ["userID"],
            parentColumns = ["userID"]
        ),
        ForeignKey(
            entity = BadgeEntity::class,
            childColumns = ["badgeID"],
            parentColumns = ["badgeID"]
        ),
        ForeignKey(
            entity = BadgeGoalEntity::class,
            childColumns = ["badgeGoalID"],
            parentColumns = ["badgeGoalID"]
        )
    ]
)
data class UserBadgeEntity(
    @PrimaryKey(autoGenerate = true) val userBadgeID: Int = 0,
    @ColumnInfo(index = true) val userID: Int,
    @ColumnInfo(index = true) val badgeID: Int,
    @ColumnInfo(index = true) val badgeGoalID: Int,
    @ColumnInfo(defaultValue = "0") val isCompleted: Boolean
)
