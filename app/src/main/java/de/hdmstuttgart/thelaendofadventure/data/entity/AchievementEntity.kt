package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievement",
    foreignKeys = [
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        ),
        ForeignKey(
            entity = QuestEntity::class,
            childColumns = ["questID"],
            parentColumns = ["questID"]
        )
    ]
)
data class AchievementEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val actionID: Int,
    @ColumnInfo(index = true) val questID: Int
)
