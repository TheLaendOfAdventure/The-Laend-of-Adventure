package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    foreignKeys = [
        ForeignKey(
            entity = ActionObjectEntity::class,
            childColumns = ["locationID"],
            parentColumns = ["actionObjectID"]
        )
    ]
)
data class LocationEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val locationID: Int,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "latitude") val latitude: Double
)
