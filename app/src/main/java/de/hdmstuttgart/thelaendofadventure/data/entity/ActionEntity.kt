package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val actionID: Int = 0,
    val name: String,
    val type: String,
    val description: String
)
