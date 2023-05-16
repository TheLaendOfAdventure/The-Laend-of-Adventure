package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserCreationBinding
import de.hdmstuttgart.thelaendofadventure.FullscreenActivity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.permissions.Permissions
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.UserCreationViewModel

class UserCreationFragment : Fragment(R.layout.fragment_user_creation) {

    private lateinit var binding: FragmentUserCreationBinding
    private lateinit var viewModel: UserCreationViewModel
    private lateinit var mPickGallery: ActivityResultLauncher<String>
    private lateinit var permissionManager: PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ActivityResultLauncher to pick an image from the gallery
        mPickGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    viewModel.saveImage(uri)
                    binding.userCreationPageAvatarButton.setImageURI(viewModel.imageUri)
                    Log.d(TAG, "User avatar image saved: $uri")
                }
            }
        permissionManager = PermissionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[UserCreationViewModel::class.java]

        // Setup Click Listeners
        setupAvatarButton()
        setupConfirmButton()
    }

    /**
     * Sets up a click listener for the avatar button, which allows users to select a profile image from the gallery.
     */
    private fun setupAvatarButton() {
        binding.userCreationPageAvatarButton.setOnClickListener {
            if (permissionManager.checkPermission(Permissions.READ_WRITE_STORAGE)) {
                pickImage()
            } else {
                Log.d(TAG, "READ_WRITE_STORAGE permission not granted")
            }
        }
    }

    /**
     * Sets up a click listener for the confirm button,
     * which creates a new user profile with the given name and profile image.
     */
    private fun setupConfirmButton() {
        binding.userCreationPageConfirmButton.setOnClickListener {
            viewModel.name = binding.nameTextInput.text.toString()
            viewModel.createUser()
            activity?.supportFragmentManager?.popBackStack()
            (activity as FullscreenActivity).setupGame()
            Log.d(TAG, "User created with name: ${viewModel.name}")
        }
    }

    /**
     * Launches an ActivityResultLauncher to pick an image from the gallery.
     */
    private fun pickImage() {
        val items = arrayOf<CharSequence>(
            getString(R.string.pick_from_gallery),
            getString(R.string.cancel)
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_image)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == getString(R.string.pick_from_gallery) -> {
                    mPickGallery.launch("image/*")
                    Log.d(TAG, "Launching gallery intent")
                }

                items[item] == getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    companion object {
        private const val TAG = "UserCreationFragment"
    }
}
