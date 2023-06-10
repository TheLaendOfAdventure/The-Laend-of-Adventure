package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserCreationBinding
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.permissions.Permissions
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.UserCreationViewModel

class UserCreationFragment : Fragment(R.layout.fragment_user_creation) {

    private lateinit var binding: FragmentUserCreationBinding
    private lateinit var viewModel: UserCreationViewModel
    private lateinit var mPickGallery: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var permissionManager: PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ActivityResultLauncher to pick an image from the gallery
        mPickGallery =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    viewModel.saveImage(uri)
                    Glide.with(requireContext())
                        .load(viewModel.imagePath)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.userCreationPageAvatarButton)
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
        Log.d(TAG, "UserCreationPage is created!")
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
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_creation_to_main_page
            )
            Log.d(TAG, "User created with name: ${viewModel.name}")
        }
    }

    /**
     * Launches an ActivityResultLauncher to pick an image from the gallery.
     */
    private fun pickImage() {
        mPickGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    companion object {
        private const val TAG = "UserCreationFragment"
    }
}
