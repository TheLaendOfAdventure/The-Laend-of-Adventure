package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application // ktlint-disable import-ordering
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

class UserCreationViewModel(private val application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "UserCreationViewModel"
    }

    lateinit var name: String
    private var imageUri: Uri = "".toUri()
    var imagePath: String = ""

    private val userRepository: UserRepository = AppDataContainer(application).userRepository
    private val badgeRepository: BadgeRepository = AppDataContainer(application).badgeRepository

    /**
     * Coroutine function for creating a new user with the given name and image path.
     */
    fun createUser() = viewModelScope.launch(Dispatchers.IO) {
        var userID: Int
        val user = UserEntity(name = name, imagePath = imagePath)

        runBlocking {
            userID = userRepository.addUser(user).toInt()
            SharedPreferencesHelper.addUser(application as Context, userID)
        }

        badgeRepository.assignAllBadgesToUser(userID)
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

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            imagePath = file.absolutePath
            imageUri = file.toUri()
            Log.d(TAG, "ImageUri has been initialized : $imageUri")

            Toast.makeText(
                getApplication(),
                "Image saved to $imagePath",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            Toast.makeText(
                getApplication(),
                R.string.failed_to_save_image,
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "Error saving image: $e")
        }
    }
}
