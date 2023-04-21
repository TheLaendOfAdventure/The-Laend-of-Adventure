package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userID: Int = 0,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "walkedKm") val walkedKm: Int?
)
