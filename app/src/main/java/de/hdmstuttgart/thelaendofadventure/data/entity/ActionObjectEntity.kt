package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actionObject")
data class ActionObjectEntity(
    @PrimaryKey(autoGenerate = true) val actionObjectID: Int = 0,
    @ColumnInfo(name = "objectType") val objectType: String,
    @ColumnInfo(name = "objectName") val objectName: String
)
