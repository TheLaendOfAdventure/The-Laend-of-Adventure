package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "riddle",
    foreignKeys = [
        ForeignKey(
            entity = ActionEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class RiddleEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val actionID: Int,
    val question: String,
    val answer: String,
    val hint: String?
)
