package de.hdmstuttgart.thelaendofadventure.ui.popupwindow

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import de.hdmstuttgart.the_laend_of_adventure.R
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
 * @param partnerImagePath The path to the image of the conversation partner.
 */
class ConversationPopupDialog(
    private val context: Context,
    private val dialogPath: String,
    userID: Int,
    private val partnerImagePath: String,
) {
    private val userRepository: UserRepository = AppDataContainer(context).userRepository
    val user = userRepository.getUserByID(userID).asLiveData()

    private lateinit var dialogueList: List<Pair<String, String>>
    private var currentIndex = 0
    private lateinit var popupView: View
    private lateinit var popupWindow: PopupWindow
    private lateinit var userTextBox: LinearLayout
    private lateinit var userTextView: TextView
    private lateinit var userProfile: ImageView
    private lateinit var userName: TextView
    private lateinit var partnerTextBox: LinearLayout
    private lateinit var partnerTextView: TextView
    private lateinit var partnerName: TextView
    private lateinit var partnerProfile: ImageView

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
        val json = JsonHelper(context, dialogPath)
        dialogueList = json.readDialogueFromJsonFile()
        val inflater = LayoutInflater.from(context)
        popupView = inflater.inflate(R.layout.conversation_popup, null)

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true,
        )

        userTextBox = popupView.findViewById(R.id.userTextbox)
        userTextView = popupView.findViewById(R.id.userTextView)
        userProfile = popupView.findViewById(R.id.userProfileImage)
        userName = popupView.findViewById(R.id.userName)

        val userObserver = Observer<UserEntity> { user ->
            updateUserData(user)
        }
        user.observeForever(userObserver)

        partnerTextBox = popupView.findViewById(R.id.partnerTextbox)
        partnerTextView = popupView.findViewById(R.id.partnerTextView)
        partnerName = popupView.findViewById(R.id.partnerName)
        partnerProfile = popupView.findViewById(R.id.partnerProfileImage)
    }

    /**
     * Sets up the popup window and its click listener.
     */
    private fun setupPopupWindow() {
        val imageName = partnerImagePath
        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        partnerProfile.setImageResource(resourceId)

        val card: View = popupView.findViewById(R.id.dialog_card)
        setupCardViewClickListener(card)

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    /**
     * Updates user data in the views.
     *
     * @param user The user entity.
     */
    private fun updateUserData(user: UserEntity) {
        userName.text = user.name
        Glide.with(userProfile.context)
            .load(user.imagePath)
            .into(userProfile)
    }

    /**
     * Displays the next dialogue in the conversation.
     */
    private fun displayNextDialogue() {
        if (currentIndex < dialogueList.size) {
            val (speaker, message) = dialogueList[currentIndex]
            if (speaker == "Player") {
                userTextBox.visibility = View.VISIBLE
                partnerTextBox.visibility = View.GONE
                userTextView.text = message
            } else {
                userTextBox.visibility = View.GONE
                partnerTextBox.visibility = View.VISIBLE
                partnerTextView.text = message
                partnerName.text = speaker
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
