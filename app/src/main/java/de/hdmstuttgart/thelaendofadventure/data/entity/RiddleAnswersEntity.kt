package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "riddleAnswers",
    foreignKeys = [
        ForeignKey(
            entity = RiddleEntity::class,
            childColumns = ["actionID"],
            parentColumns = ["actionID"]
        )
    ]
)
data class RiddleAnswersEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val actionID: Int,
    val answer: String
)
