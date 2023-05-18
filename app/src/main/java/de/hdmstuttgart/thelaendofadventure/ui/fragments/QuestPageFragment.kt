package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentQuestPageBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.ui.adapters.QuestAdapter
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.QuestPageViewModel

class QuestPageFragment : Fragment() {

    private lateinit var binding: FragmentQuestPageBinding
    private lateinit var viewModel: QuestPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[QuestPageViewModel::class.java]

        binding = FragmentQuestPageBinding.inflate(inflater, container, false)

        val recycleView = binding.questPageRecyclerview
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        val questObserver = Observer<List<QuestDetails>> { questList ->
            // Handle the questList
            val adapter = QuestAdapter(questList)

            recycleView.adapter = adapter
        }

        val userObserver = Observer<UserEntity> { user ->
            binding.questProfileButtonLevelDisplay.text = user.level.toString()
            binding.questPageProfileButton.setImageURI(user.imagePath?.toUri())
        }
        viewModel.user.observe(viewLifecycleOwner, userObserver)

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.questList.observe(viewLifecycleOwner, questObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserPageNavigationButtons()
        setUpBadgePageProfileButton()
    }

    private fun setUpBadgePageProfileButton() {
        binding.questPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_quest_to_main_page
            )
        }
    }

    private fun setUpUserPageNavigationButtons() {
        binding.questPageNavigationButtonToUser.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_quest_to_user_page
            )
        }
        binding.questPageNavigationButtonToBadges.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_quest_to_badges_page
            )
        }
    }
}
