package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application // ktlint-disable import-ordering
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class UserCreationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "UserCreationViewModel"
    }

    // Repository for accessing user data
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    // User name
    lateinit var name: String

    // User image URI and file path
    var imageUri: Uri = "".toUri()
    var imagePath: String = ""

    /**
     * Coroutine function for creating a new user with the given name and image path.
     */
    fun createUser() = viewModelScope.launch(Dispatchers.IO) {
        val user = UserEntity(name = name, imagePath = imagePath)
        userRepository.addUser(user)
    }

    /**
     * Saves an image from the given URI to the app's files directory and updates [imageUri] and [imagePath].
     * @param uri The URI of the image to save.
     */
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
}
