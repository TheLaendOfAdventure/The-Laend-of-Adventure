package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.hdmstuttgart.the_laend_of_adventure.R

/**
 * The PermissionManager class provides methods to check and request permissions.
 */
class PermissionManager(private val context: Context) {

    private val isTiramisuOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    companion object {
        const val STORAGE_PERMISSION_CODE = 1
        const val READ_MEDIA_IMAGES_PERMISSION_CODE = 2
        const val FINE_LOCATION_PERMISSION_CODE = 4
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
                val permissionCode: Int =
                    if (isTiramisuOrHigher) {
                        READ_MEDIA_IMAGES_PERMISSION_CODE
                    } else {
                        STORAGE_PERMISSION_CODE
                    }
                requestPermissions(getStoragePermissions(), permissionCode)
                false
            }
        }

        Permissions.LOCATION -> {
            if (checkFineLocationPermission()) {
                true
            } else {
                requestPermissions(getFineLocationPermissions(), FINE_LOCATION_PERMISSION_CODE)
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

    private fun checkFineLocationPermission(): Boolean {
        val permissions = getFineLocationPermissions()

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        val activity = context as Activity

        val shouldShowRationale = permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(R.string.gps_required_title)
            .setMessage(R.string.gps_required_context)
            .setPositiveButton(R.string.gps_positiveButton) { dialog, _ ->
                activity.requestPermissions(permissions, requestCode)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.gps_negativeButton) { dialog, _ ->
                dialog.dismiss()
            }

        if (shouldShowRationale) {
            dialogBuilder.create().show()
        } else {
            activity.requestPermissions(permissions, requestCode)
        }
    }

    private fun getStoragePermissions(): Array<String> = if (isTiramisuOrHigher) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun getFineLocationPermissions(): Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}

/**
 * Represents a permission that can be checked or requested.
 */
enum class Permissions {
    READ_WRITE_STORAGE,
    LOCATION
}
