package de.hdmstuttgart.thelaendofadventure.ui.popupwindow

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import de.hdmstuttgart.the_laend_of_adventure.databinding.ConversationPopupBinding
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.JsonHelper

/**
 * A popup dialog that displays a conversation between the user and a partner.
 * It reads the conversation dialogue from a JSON file and displays it in a popup window.
 *
 * @param context The context of the application.
 * @param dialogPath The path to the JSON file containing the conversation dialogue.
 * @param userID The ID of the user participating in the conversation.
 */
class ConversationPopupDialog(
    private val context: Context,
    private val dialogPath: String,
    userID: Int
) {
    private val userRepository: UserRepository = AppDataContainer(context).userRepository
    val user = userRepository.getUserByID(userID).asLiveData()

    private var currentIndex = 0
    private lateinit var popupWindow: PopupWindow

    private val json = JsonHelper(context, dialogPath)
    private var dialogueList = json.readDialogueFromJsonFile()
    private val imageName = json.readNpcImgFromJsonFile()
    private lateinit var binding: ConversationPopupBinding

    /**
     * Shows the conversation popup dialog.
     */
    fun show() {
        initializeViews()
        setupPopupWindow()
        displayNextDialogue()
    }

    /**
     * Initializes views and sets up necessary observers.
     */
    @SuppressLint("InflateParams")
    private fun initializeViews() {
        binding = ConversationPopupBinding.inflate(LayoutInflater.from(context))
        popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val userObserver = Observer<UserEntity> { user ->
            updateUserData(user)
        }
        user.observeForever(userObserver)
    }

    /**
     * Sets up the popup window and its click listener.
     */
    @SuppressLint("DiscouragedApi")
    private fun setupPopupWindow() {
        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        binding.partnerProfileImage.setImageResource(resourceId)

        val card: View = binding.dialogCard
        setupCardViewClickListener(card)

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
    }

    /**
     * Updates user data in the views.
     *
     * @param user The user entity.
     */
    private fun updateUserData(user: UserEntity) {
        binding.userName.text = user.name
        Glide.with(context)
            .load(user.imagePath)
            .into(binding.userProfileImage)
    }

    /**
     * Displays the next dialogue in the conversation.
     */
    private fun displayNextDialogue() {
        if (currentIndex < dialogueList.size) {
            val (speaker, message) = dialogueList[currentIndex]
            if (speaker == "Player") {
                binding.userBackgroundContainer.visibility = View.VISIBLE
                binding.userTextbox.visibility = View.VISIBLE
                binding.partnerBackgroundContainer.visibility = View.GONE
                binding.partnerTextbox.visibility = View.GONE
                binding.userTextView.text = message
            } else {
                binding.userBackgroundContainer.visibility = View.GONE
                binding.userTextbox.visibility = View.GONE
                binding.partnerBackgroundContainer.visibility = View.VISIBLE
                binding.partnerTextbox.visibility = View.VISIBLE
                binding.partnerTextView.text = message
                binding.partnerName.text = speaker
            }
        }
        currentIndex++
    }

    /**
     * Sets up a click listener for the dialog card to display the next dialogue.
     *
     * @param card The dialog card view.
     */
    private fun setupCardViewClickListener(card: View) {
        card.setOnClickListener {
            displayNextDialogue()
            if (currentIndex > dialogueList.size) {
                popupWindow.dismiss()
                dismissListener?.invoke()
            }
        }
    }

    private var dismissListener: (() -> Unit)? = null
    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }
}
