package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class UserPageViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    lateinit var name: String
    var imageUri: Uri = "".toUri()
    var imagePath: String = ""

    val userID = application.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)

    val user = userRepository.getUserByID(userID).asLiveData()

    fun saveImage(uri: Uri) {
        try {
            // Create a new file in the app's files directory
            val file = File(
                getApplication<Application>().filesDir,
                "new_image.jpg"
            )

            // Open an input stream for the image URI and an output stream for the new file
            val inputStream: InputStream? =
                getApplication<Application>().contentResolver.openInputStream(uri)
            val outputStream: OutputStream

            // Copy the input stream to the output stream
            outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }

            // Update the image URI and file path
            imageUri = file.toUri()
            Log.d(TAG, "ImageUri has been initialized : $imageUri")
            imagePath = file.absolutePath

            // Show a toast message confirming the image was saved
            Toast.makeText(
                getApplication(),
                "Image saved to $imagePath",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            // Show a toast message if there was an error saving the image
            Toast.makeText(
                getApplication(),
                (R.string.failed_to_save_image),
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "Error saving image: $e")
        }
    }

    suspend fun updateUserData(name: String) {
        userRepository.updateUserName(userID, name)
    }

    suspend fun updateUserImage(imagePath: String) {
        userRepository.updateUserImagePath(userID, imagePath)
    }

    companion object {
        const val TAG = "UserPageViewModel"
    }
}
