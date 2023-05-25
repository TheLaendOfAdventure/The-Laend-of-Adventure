package de.hdmstuttgart.thelaendofadventure.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.FragmentRiddlePopupBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.ui.viewmodels.RiddlePopupViewModel

class RiddlePopupFragment(private val riddles: List<RiddleDetails>) : DialogFragment() {

    companion object {
        private const val questID = 7
        private const val questGoal = 2
        private const val answer3 = 3
    }

    private lateinit var binding: FragmentRiddlePopupBinding
    private lateinit var viewModel: RiddlePopupViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.fragment_riddle_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
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

        setupViews()
        setOnClickListeners()
    }

    private fun setupViews() {
        val answers = riddles.map { riddle -> riddle.possibleAnswers }

        binding.riddleTextView.text = riddles[0].question
        binding.answerOption1.text = answers[0]
        binding.answerOption2.text = answers[1]
        binding.answerOption3.text = answers[2]
        binding.answerOption4.text = answers[answer3]
    }

    private fun setOnClickListeners() {
        binding.answerOption1.setOnClickListener {
            QuestLogic(requireContext()).finishedQuestGoal(questID, questGoal, viewModel.userID)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    private var dismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }
}
