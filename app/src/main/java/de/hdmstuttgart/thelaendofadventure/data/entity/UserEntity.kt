package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userID: Int = 0,
    val name: String?,
    val imagePath: String?,
    val walkedKm: Int?,
    val level: Int = 1,
    val exp: Int = 0
)
