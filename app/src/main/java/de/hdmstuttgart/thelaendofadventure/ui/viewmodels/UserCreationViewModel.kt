package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository

class UserCreationViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = AppDataContainer(application).userRepository
    lateinit var name: String
    lateinit var uri: String

    fun createUser() {
        val user = UserEntity(name = name, imagePath = uri)
        userRepository.addUser(user)
    }
}
