package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import kotlinx.coroutines.runBlocking

class UserPageViewModel(application: Application) : AndroidViewModel(application) {
    /*
    companion object {
        const val TAG = "UserPageViewModel"
    }
     */
    private val userRepository: UserRepository = AppDataContainer(application).userRepository

    lateinit var name: String
    lateinit var imagePath: String
    var level: Int = 0

    /* val userID = application.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)
     */
    var userID = 0

    /**
     * This method fetches the current users data and assigns it to the variables
     * */
    fun getUserData() = runBlocking {
        val user = userRepository.getUserByID(userID)
        user.collect {
            name = it.name.toString()
            imagePath = it.imagePath.toString()
            level = it.level
        }
    }

    // Change TextViews to Input Fields
    /*
    fun updateUserName() {
    }
    fun updateUserImage() {
    }
    */
    // navigate to main page again
}
