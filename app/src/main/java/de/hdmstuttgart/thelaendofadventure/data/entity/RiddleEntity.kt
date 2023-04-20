package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "riddle",
    foreignKeys = [
        ForeignKey(
            entity = ActionObjectEntity::class,
            childColumns = ["riddleID"],
            parentColumns = ["actionObjectID"]
        )
    ]
)
data class RiddleEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val riddleID: Int,
    @ColumnInfo(name = "question") val question: String,
    @ColumnInfo(name = "answer") val answer: String,
    @ColumnInfo(name = "hint") val hint: String
)
