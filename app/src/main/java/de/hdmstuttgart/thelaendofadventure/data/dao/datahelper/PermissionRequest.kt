package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class PermissionRequest(
    var permissions: Array<String>,
    var requestCode: Int,
    val title: Int,
    val message: Int,
    val positiveButton: Int,
    val negativeButton: Int,
    val checkPermissionAfterRequest: Boolean = false,
    val executeApp: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionRequest

        if (!permissions.contentEquals(other.permissions)) return false
        if (requestCode != other.requestCode) return false
        if (title != other.title) return false
        if (message != other.message) return false
        if (positiveButton != other.positiveButton) return false
        if (negativeButton != other.negativeButton) return false
        if (checkPermissionAfterRequest != other.checkPermissionAfterRequest) return false
        if (executeApp != other.executeApp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = permissions.contentHashCode()
        result = 31 * result + requestCode
        result = 31 * result + title
        result = 31 * result + message
        result = 31 * result + positiveButton
        result = 31 * result + negativeButton
        result = 31 * result + checkPermissionAfterRequest.hashCode()
        result = 31 * result + executeApp.hashCode()
        return result
    }
}
