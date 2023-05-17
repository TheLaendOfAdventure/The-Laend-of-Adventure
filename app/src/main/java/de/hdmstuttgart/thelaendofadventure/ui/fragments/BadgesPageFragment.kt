package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentBadgesPageBinding

// @TODO Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class BadgesPageFragment : Fragment() {

    private lateinit var binding: FragmentBadgesPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // @TODO recycleview implementation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_badges_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserPageNavigationButtons()
    }

    private fun setUpUserPageNavigationButtons() {
        binding.badgesPageNavigationButtonToUser.setOnClickListener {
            // TODO implement as badges page gets implemented
        }
        binding.badgesPageNavigationButtonToQuest.setOnClickListener {
            // TODO implement as quest page gets implemented
        }
    }
}
