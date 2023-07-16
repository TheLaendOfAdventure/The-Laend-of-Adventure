package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application // ktlint-disable import-ordering
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class UserPageViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    lateinit var name: String
    private var imageUri: Uri = "".toUri()
    var imagePath: String = ""

    val userID = SharedPreferencesHelper.getUserID(application as Context)

    val user = userRepository.getUserByID(userID).asLiveData()

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

            viewModelScope.launch {
                updateUserImage(imagePath)
            }

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

    /*
    private suspend fun updateUserData(name: String) {
        userRepository.updateUserName(userID, name)
    }
    */

    private fun updateUserImage(imagePath: String) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.updateUserImagePath(userID, imagePath)
    }

    fun updateUserName(newName: String) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.updateUserName(userID, newName)
    }

    companion object {
        const val TAG = "UserPageViewModel"
    }
}
