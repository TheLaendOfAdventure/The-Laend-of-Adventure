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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserPageBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.ui.helper.PermissionManager
import de.hdmstuttgart.thelaendofadventure.ui.helper.Permissions
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
                    Glide.with(requireContext())
                        .load(viewModel.imagePath)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.userPageProfilePictureView)
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
            binding.userPageNameField.setText(user.name)
            binding.userPageLevelDisplay.text = user.level.toString()
            binding.userPageProgressBar.progress = user.exp
            binding.userPageExperienceNumeric.text = requireContext().getString(
                R.string.level_progress_numeric_text,
                user.exp
            )

            Glide.with(requireContext())
                .load(user.imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.userPageProfilePictureView)
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
            viewModel.updateUserName(binding.userPageNameField.text.toString())
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_main_page
            )
        }
    }

    private fun setUpUserPageNavigationButtons() {
        binding.userPageNavigationButtonToBadges.setOnClickListener {
            viewModel.updateUserName(binding.userPageNameField.text.toString())
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_badges_page
            )
        }
        binding.userPageNavigationButtonToQuest.setOnClickListener {
            viewModel.updateUserName(binding.userPageNameField.text.toString())
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_user_to_quest_page
            )
        }
    }

    companion object {
        private const val TAG = "UserPageFragment"
    }
}
