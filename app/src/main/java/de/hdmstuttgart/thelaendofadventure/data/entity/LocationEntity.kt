package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    foreignKeys = [
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class LocationEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val actionID: Int,
    val longitude: Double,
    val latitude: Double
)
