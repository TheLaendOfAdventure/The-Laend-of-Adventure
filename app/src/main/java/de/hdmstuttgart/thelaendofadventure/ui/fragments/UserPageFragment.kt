package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserPageBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.permissions.PermissionManager
import de.hdmstuttgart.thelaendofadventure.permissions.Permissions
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.UserPageViewModel

class UserPageFragment : Fragment(R.layout.fragment_user_page) {

    private lateinit var binding: FragmentUserPageBinding
    private lateinit var viewModel: UserPageViewModel
    private lateinit var mPickGallery: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var permissionManager: PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ActivityResultLauncher to pick an image from the gallery
        mPickGallery =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    viewModel.saveImage(uri)
                    binding.userPageProfilePictureView.setImageURI(viewModel.imageUri)
                    binding.userPageProfileButton.setImageURI(viewModel.imageUri)
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
        binding = FragmentUserPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[UserPageViewModel::class.java]

        val userObserver = Observer<UserEntity> { user ->
            binding.userPageNameField.hint = user.name
            binding.userPageLevelDisplay.text = user.level.toString()
            binding.userPageProfileButtonLevelDisplay.text = user.level.toString()
            binding.userPageProfileButton.setImageURI(user.imagePath?.toUri())
            binding.userPageProfilePictureView.setImageURI(user.imagePath?.toUri())
        }
        viewModel.user.observe(viewLifecycleOwner, userObserver)

        changeImageButton()
        setUpUserPageProfileButton()
        setUpUserPageNavigationButtons()
    }

    private fun changeImageButton() {
        binding.userPageProfilePictureView.setOnClickListener {
            if (permissionManager.checkPermission(Permissions.READ_WRITE_STORAGE)) {
                pickImage()
            } else {
                Log.d(TAG, "READ_WRITE_STORAGE permission not granted")
            }
        }
    }

    private fun pickImage() {
        mPickGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun setUpUserPageProfileButton() {
        binding.userPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_main_page
            )
        }
    }

    private fun setUpUserPageNavigationButtons() {
        binding.userPageNavigationButtonToBadges.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_badges_page
            )
        }
        binding.userPageNavigationButtonToQuest.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_quest_page
            )
        }
    }

    companion object {
        private const val TAG = "UserPageFragment"
    }
}
