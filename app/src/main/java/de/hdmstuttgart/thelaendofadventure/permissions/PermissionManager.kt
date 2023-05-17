package de.hdmstuttgart.thelaendofadventure.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * The PermissionManager class provides methods to check and request permissions.
 */
class PermissionManager(private val context: Context) {

    private val isTiramisuOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    companion object {
        const val STORAGE_PERMISSION_CODE = 1
        const val READ_MEDIA_IMAGES_PERMISSION_CODE = 2
    }

    /**
     * Checks if the given permission is granted. If the permission is not granted,
     * it requests the permission and returns false.
     *
     * @param permission the permission to check
     * @return true if the permission is granted, false otherwise
     */
    fun checkPermission(permission: Permissions): Boolean = when (permission) {
        Permissions.READ_WRITE_STORAGE -> {
            if (checkStoragePermission()) {
                true
            } else {
                requestPermissions()
                false
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        val permissions = if (isTiramisuOrHigher) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val requestCode = if (isTiramisuOrHigher) {
            STORAGE_PERMISSION_CODE
        } else {
            READ_MEDIA_IMAGES_PERMISSION_CODE
        }
        (context as Activity).requestPermissions(getPermissions(), requestCode)
    }

    private fun getPermissions() = if (isTiramisuOrHigher) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

/**
 * Represents a permission that can be checked or requested.
 */
enum class Permissions {
    READ_WRITE_STORAGE
}
