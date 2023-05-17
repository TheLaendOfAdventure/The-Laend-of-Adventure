package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application // ktlint-disable import-ordering
import android.content.Context
import android.content.SharedPreferences
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
import java.io.IOException

class UserCreationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "UserCreationViewModel"
    }

    // Repository for accessing user data
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    // Saving the userID to SharedPreferences
    private val sharedPreferences =
        application.getSharedPreferences(
            R.string.sharedPreferences.toString(),
            Context.MODE_PRIVATE
        )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

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
        val userID = userRepository.addUser(user).toInt()

        editor.putInt(R.string.userID.toString(), userID)
        editor.apply()
    }

    /**
     * Saves an image from the given URI to the app's files directory and updates [imageUri] and [imagePath].
     * @param uri The URI of the image to save.
     */
    fun saveImage(uri: Uri) {
        try {
            val context = getApplication<Application>()
            val filename = "profile_image.jpg"
            val dir = context.getDir("my_images", Context.MODE_PRIVATE)
            val file = File(dir, filename)

            // Copy the input stream to the output stream using useLines and use extension functions
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Update the image URI and file path
            imagePath = file.absolutePath
            imageUri = file.toUri()
            Log.d(TAG, "ImageUri has been initialized : $imageUri")

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
                R.string.failed_to_save_image,
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "Error saving image: $e")
        }
    }
}
