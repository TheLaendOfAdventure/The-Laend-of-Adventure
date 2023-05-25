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
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentBadgesPageBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity
import de.hdmstuttgart.thelaendofadventure.data.entity.UserEntity
import de.hdmstuttgart.thelaendofadventure.ui.adapters.BadgesAdapter
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.BadgesPageViewModel

class BadgesPageFragment : Fragment() {

    private lateinit var binding: FragmentBadgesPageBinding
    private lateinit var viewModel: BadgesPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BadgesPageViewModel::class.java]

        binding = FragmentBadgesPageBinding.inflate(inflater, container, false)

        val acceptedRecycleView = binding.badgesPageRecyclerview
        acceptedRecycleView.layoutManager = LinearLayoutManager(requireContext())
        acceptedRecycleView.adapter = BadgesAdapter(emptyList(), accepted = true)

        val acceptedBadgeObserver = Observer<List<BadgeEntity>> { badgeList ->
            // Handle the badgeList
            val adapter = BadgesAdapter(badgeList, accepted = true)
            acceptedRecycleView.adapter = adapter
            println(badgeList)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.acceptedBadges.observe(viewLifecycleOwner, acceptedBadgeObserver)

        val unacceptedRecycleView = binding.badgesPageRecyclerview
        unacceptedRecycleView.layoutManager = LinearLayoutManager(requireContext())
        unacceptedRecycleView.adapter = BadgesAdapter(emptyList(), accepted = false)

        val unacceptedBadgeObserver = Observer<List<BadgeEntity>> { badgeList ->
            // Handle the badgeList
            val adapter = BadgesAdapter(badgeList, accepted = false)
            unacceptedRecycleView.adapter = adapter
            println(badgeList)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.unacceptedBadges.observe(viewLifecycleOwner, unacceptedBadgeObserver)

        val userObserver = Observer<UserEntity> { user ->
            binding.badgesProfileButtonLevelDisplay.text = user.level.toString()
            binding.badgesPageProfileButton.setImageURI(user.imagePath?.toUri())
        }
        viewModel.user.observe(viewLifecycleOwner, userObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserPageNavigationButtons()
        setUpBadgePageProfileButton()
    }

    private fun setUpBadgePageProfileButton() {
        binding.badgesPageProfileButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_badges_to_main_page
            )
        }
    }

    private fun setUpUserPageNavigationButtons() {
        binding.badgesPageNavigationButtonToUser.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_badges_to_user_page
            )
        }
        binding.badgesPageNavigationButtonToQuest.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_badges_to_quest_page
            )
        }
    }
}
