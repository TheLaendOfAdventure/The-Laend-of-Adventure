package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
            val adapter = QuestAdapter(questList, this)

            recycleView.adapter = adapter
        }
        viewModel.questList.observe(viewLifecycleOwner, questObserver)

        val userObserver = Observer<UserEntity> { user ->
            binding.questProfileButtonLevelDisplay.text = user.level.toString()
            Glide.with(requireContext())
                .load(user.imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.questPageProfileButton)
        }
        viewModel.user.observe(viewLifecycleOwner, userObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpQuestPageNavigationButtons()
        setUpQuestPageProfileButton()
    }

    private fun setUpQuestPageProfileButton() {
        binding.questPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_quest_to_main_page
            )
        }
    }

    private fun setUpQuestPageNavigationButtons() {
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
