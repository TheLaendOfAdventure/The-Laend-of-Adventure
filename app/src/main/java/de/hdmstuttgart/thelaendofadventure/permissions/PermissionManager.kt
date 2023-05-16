package de.hdmstuttgart.thelaendofadventure.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * The PermissionManager class provides methods to check and request permissions.
 */
class PermissionManager(private val context: Context) {

    companion object {
        const val STORAGE_PERMISSION_CODE = 1
    }

    /**
     * Checks if the given permission is granted. If the permission is not granted,
     * it requests the permission and returns false.
     *
     * @param permission the permission to check
     * @return true if the permission is granted, false otherwise
     */
    fun checkPermission(permission: Permissions): Boolean {
        return when (permission) {
            Permissions.READ_WRITE_STORAGE -> {
                if (!checkStoragePermission()) {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permissions, STORAGE_PERMISSION_CODE)
                    false
                } else {
                    true
                }
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        val readStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val writeStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return readStorage && writeStorage
    }

    private fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(context as Activity, permissions, requestCode)
    }
}

/**
 * Represents a permission that can be checked or requested.
 */
enum class Permissions {
    READ_WRITE_STORAGE
}
