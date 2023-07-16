package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badge")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val badgeID: Int = 0,
    val name: String,
    val description: String,
    val imagePath: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BadgeEntity

        return name == other.name &&
            description == other.description &&
            imagePath == other.imagePath
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + imagePath.hashCode()
        return result
    }
}
