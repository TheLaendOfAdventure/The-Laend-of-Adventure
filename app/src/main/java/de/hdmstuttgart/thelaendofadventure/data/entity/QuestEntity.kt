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
    val latitude: Double,
    val level: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuestEntity

        return name == other.name &&
            imagePath == other.imagePath &&
            dialogPath == other.dialogPath &&
            description == other.description &&
            targetGoalNumber == other.targetGoalNumber &&
            longitude == other.longitude &&
            latitude == other.latitude &&
            level == other.level
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (imagePath?.hashCode() ?: 0)
        result = 31 * result + dialogPath.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + targetGoalNumber
        result = 31 * result + longitude.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + level
        return result
    }
}
