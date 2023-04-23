package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quest")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val questID: Int = 0,
    @ColumnInfo(index = true) val userID: Int?,
    val name: String,
    val imagePath: String?,
    val dialogPath: String,
    val description: String,
    val currentGoalNumber: Int = 0,
    val targetGoalNumber: Int
)
