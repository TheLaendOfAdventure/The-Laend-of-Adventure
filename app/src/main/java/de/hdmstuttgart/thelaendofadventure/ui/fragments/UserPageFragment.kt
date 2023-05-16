package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserPageBinding
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.UserPageViewModel
import java.io.File

class UserPageFragment : Fragment(R.layout.fragment_user_page) {

    private lateinit var binding: FragmentUserPageBinding
    private lateinit var viewModel: UserPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // User data is retrieved
        viewModel.getUserData()

        // User data is bound to the XML Layout objects
        binding.userPageNameView.text = viewModel.name
        binding.userPageLevelDisplay.text = viewModel.level.toString()
        binding.userPageProfileButtonLevelDisplay.text = viewModel.level.toString()
        val imageUri = Uri.fromFile(File(viewModel.imagePath))
        binding.userPageProfilePictureView.setImageURI(imageUri)

        setUpUserPageExitButton()
        setUpChangeButton()
    }

    private fun setUpUserPageExitButton() {
        binding.userPageProfileButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    /**
     * This method changes the TextViews to User Input Fields and creates a confirm button*/
    private fun setUpChangeButton() {
        // A Button is needed in the XML File
        // binding.userPageSettingsButton.
    }
}
