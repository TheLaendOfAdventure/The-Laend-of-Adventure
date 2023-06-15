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
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentBadgesPageBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
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

        val completedRecycleView = binding.badgesPageRecyclerviewAccepted
        completedRecycleView.layoutManager = LinearLayoutManager(requireContext())
        completedRecycleView.adapter = BadgesAdapter(emptyList(), completed = true, this)

        val completedBadgeObserver = Observer<List<BadgeDetails>> { badgeList ->
            // Handle the accepted badgeList
            val adapter = BadgesAdapter(badgeList, completed = true, this)
            completedRecycleView.adapter = adapter
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.completedBadges.observe(viewLifecycleOwner, completedBadgeObserver)

        val unCompletedRecycleView = binding.badgesPageRecyclerviewUnaccepted
        unCompletedRecycleView.layoutManager = LinearLayoutManager(requireContext())
        unCompletedRecycleView.adapter = BadgesAdapter(emptyList(), completed = false, this)

        val unCompletedBadgeObserver = Observer<List<BadgeDetails>> { badgeList ->
            // Handle the unaccepted badgeList
            val adapter = BadgesAdapter(badgeList, completed = false, this)
            unCompletedRecycleView.adapter = adapter
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.unCompletedBadges.observe(viewLifecycleOwner, unCompletedBadgeObserver)

        val userObserver = Observer<UserEntity> { user ->
            binding.badgesProfileButtonLevelDisplay.text = user.level.toString()
            Glide.with(requireContext())
                .load(user.imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.badgesPageProfileButton)
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
