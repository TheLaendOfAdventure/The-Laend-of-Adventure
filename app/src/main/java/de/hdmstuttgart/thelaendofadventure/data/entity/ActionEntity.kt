package de.hdmstuttgart.thelaendofadventure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val actionID: Int = 0,
    val name: String,
    val type: String,
    val description: String,
    val dialogPath: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionEntity) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (description != other.description) return false
        if (dialogPath != other.dialogPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (dialogPath?.hashCode() ?: 0)
        return result
    }
}
