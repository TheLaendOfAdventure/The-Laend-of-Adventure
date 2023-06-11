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
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.PermissionRequest
import kotlin.system.exitProcess

/**
 * The PermissionManager class provides methods to check and request permissions.
 */
class PermissionManager(private val context: Context) {

    private val isTiramisuOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private var permissionCheckExecuted = false

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
                if (isTiramisuOrHigher) {
                    STORAGE_REQUEST.permissions = MEDIA_PERMISSION
                    STORAGE_REQUEST.requestCode = READ_MEDIA_IMAGES_PERMISSION_CODE
                }
                requestPermission(STORAGE_REQUEST)
                false
            }
        }

        Permissions.LOCATION -> {
            if (checkFineLocationPermission()) {
                true
            } else {
                requestPermission(LOCATION_REQUEST)
                false
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        val permissions = if (isTiramisuOrHigher) {
            MEDIA_PERMISSION
        } else {
            STORAGE_REQUEST.permissions
        }

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkFineLocationPermission(): Boolean {
        val permissions = LOCATION_REQUEST.permissions

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissionRequest: PermissionRequest) {
        val activity = context as Activity

        val shouldShowRationale = permissionRequest.permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(permissionRequest.title)
            .setMessage(permissionRequest.message)
            .setPositiveButton(permissionRequest.positiveButton) { dialog, _ ->
                activity.requestPermissions(
                    permissionRequest.permissions,
                    permissionRequest.requestCode
                )
                dialog.dismiss()
            }
            .setNegativeButton(permissionRequest.negativeButton) { dialog, _ ->
                if (permissionRequest.executeApp) {
                    exitProcess(0)
                }
                dialog.dismiss()
            }

        if (shouldShowRationale) {
            dialogBuilder.create().show()
        } else {
            activity.requestPermissions(
                permissionRequest.permissions,
                permissionRequest.requestCode
            )
            if (permissionRequest.checkPermissionAfterRequest && !permissionCheckExecuted) {
                checkPermission(Permissions.LOCATION)
                permissionCheckExecuted = true
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val READ_MEDIA_IMAGES_PERMISSION_CODE = 2
        private const val FINE_LOCATION_PERMISSION_CODE = 3
        private val MEDIA_PERMISSION = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

        private val LOCATION_REQUEST = PermissionRequest(
            permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestCode = FINE_LOCATION_PERMISSION_CODE,
            title = R.string.gps_required_title,
            message = R.string.gps_required_context,
            positiveButton = R.string.gps_positiveButton,
            negativeButton = R.string.gps_negativeButton,
            checkPermissionAfterRequest = true,
            executeApp = true
        )

        private val STORAGE_REQUEST = PermissionRequest(
            permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            requestCode = STORAGE_PERMISSION_CODE,
            title = R.string.storage_required_title,
            message = R.string.storage_required_context,
            positiveButton = R.string.storage_positiveButton,
            negativeButton = R.string.storage_negativeButton
        )
    }
}

/**
 * Represents a permission that can be checked or requested.
 */
enum class Permissions {
    READ_WRITE_STORAGE,
    LOCATION
}
