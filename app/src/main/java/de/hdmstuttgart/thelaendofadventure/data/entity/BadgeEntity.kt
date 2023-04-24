package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badge")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val badgeID: Int = 0,
    val name: String,
    val description: String,
    val imagePath: String,
    val targetGoalNumber: Int
)
