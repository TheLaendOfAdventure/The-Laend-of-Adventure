package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "statTracking",
    foreignKeys = [
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class StatTrackingEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val actionID: Int,
    val goal: Int,
    val goalUnit: String
)
