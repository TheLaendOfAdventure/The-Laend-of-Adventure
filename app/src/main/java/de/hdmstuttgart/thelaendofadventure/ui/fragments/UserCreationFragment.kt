package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.app.AlertDialog // ktlint-disable import-ordering
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentUserCreationBinding
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.UserCreationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class UserCreationFragment : Fragment(R.layout.fragment_user_creation) {

    private lateinit var binding: FragmentUserCreationBinding
    private lateinit var viewModel: UserCreationViewModel
    private lateinit var mPickGallery: ActivityResultLauncher<String>
    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPickGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    saveImage(uri)
                }
            }
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

        viewModel = ViewModelProvider(this)[UserCreationViewModel::class.java]

        binding.userCreationPageAvatarButton.setOnClickListener {
            pickImage()
        }

        binding.userCreationPageConfirmButton.setOnClickListener {
            viewModel.name = binding.nameTextInput.text.toString()
            viewModel.uri = imagePath
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.createUser()
            }
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun pickImage() {
        val items = arrayOf<CharSequence>("Aus der Galerie wählen!", "Abbrechen")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bild hinzufügen!")
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Aus der Galerie wählen!" -> {
                    mPickGallery.launch("image/*")
                }

                items[item] == "Abbrechen" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun showImageInImageView(imageUri: Uri) {
        binding.userCreationPageAvatarButton.setImageURI(imageUri)
    }

    private fun saveImage(uri: Uri) {
        try {
            val file = File(
                requireContext().filesDir,
                "new_image.jpg"
            )
            val inputStream: InputStream? =
                requireContext().contentResolver.openInputStream(uri)
            val outputStream: OutputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }

            imagePath = file.absolutePath
            showImageInImageView(file.toUri())

            Toast.makeText(
                requireContext(),
                "Image saved to $imagePath",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: FileSystemException) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT)
                .show()
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        private const val TAG = "UserCreationFragment"
    }
}
