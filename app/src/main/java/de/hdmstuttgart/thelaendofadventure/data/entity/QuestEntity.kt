package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.*

@Entity(
    tableName = "quest",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            childColumns = ["userID"],
            parentColumns = ["userID"]
        )
    ]
)
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val questID: Int,
    @ColumnInfo(name = "userID", index = true) val userID: Int?,
    @ColumnInfo(name = "questName") val questName: String,
    @ColumnInfo(name = "description") val description: String
)
