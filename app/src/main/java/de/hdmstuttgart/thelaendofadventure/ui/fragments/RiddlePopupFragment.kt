package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentRiddlePopupBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.RiddlePopupViewModel

class RiddlePopupFragment : Fragment(R.layout.fragment_riddle_popup) {
    private lateinit var binding: FragmentRiddlePopupBinding
    private lateinit var viewModel: RiddlePopupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiddlePopupBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[RiddlePopupViewModel::class.java]

        val riddleObserver = Observer<List<RiddleDetails>> { riddles ->
            if (riddles.isNotEmpty()) {
                Log.d("Popup", "List is not Empty!")
                val answers = riddles.map { riddle ->
                    riddle.possibleAnswers
                }
                binding.riddleTextView.text = riddles[0].question
                binding.answerOption1.text = answers[0]
                binding.answerOption2.text = answers[1]
                binding.answerOption3.text = answers[2]
                binding.answerOption4.text = answers[3]
                // showRiddlePopup()
            }
        }
        binding.answerOption1.setOnClickListener {
            QuestLogic(requireContext()).finishedQuestGoal(1, 2, 1)
            Navigation.findNavController(requireView()).navigate(
                R.id.navigate_from_riddle_to_main_page
            )
        }
        viewModel.riddleList.observe(viewLifecycleOwner, riddleObserver)
    }

    fun showRiddlePopup() {
        Log.d("Popup", "Visibilty is visible")
        binding.riddleMainLayout.visibility = View.VISIBLE
    }
}
