package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quest")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val questID: Int = 0,
    val name: String,
    val imagePath: String?,
    val dialogPath: String,
    val description: String,
    val targetGoalNumber: Int,
    val longitude: Double,
    val latitude: Double
)
