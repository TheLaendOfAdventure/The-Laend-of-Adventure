package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentBadgesPageBinding
import de.hdmstuttgart.thelaendofadventure.ui.adapters.BadgesAdapter
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.BadgesPageViewModel

class BadgesPageFragment : Fragment() {

    private lateinit var binding: FragmentBadgesPageBinding
    private lateinit var viewModel: BadgesPageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // @TODO recycleview implementation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BadgesPageViewModel::class.java]

        binding = FragmentBadgesPageBinding.inflate(inflater, container, false)

        val recycleView = binding.badgesPageRecyclerview
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = BadgesAdapter()
        recycleView.adapter = adapter

        viewModel.badges.observe(
            viewLifecycleOwner
        ) {
            adapter.submitList(it)
        }
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
            // TODO implement as quest page gets implemented
        }
    }
}
