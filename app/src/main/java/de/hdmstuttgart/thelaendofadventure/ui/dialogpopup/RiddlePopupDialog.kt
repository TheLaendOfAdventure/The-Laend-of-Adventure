package de.hdmstuttgart.thelaendofadventure.ui.dialogpopup

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import de.hdmstuttgart.the_laend_of_adventure.databinding.DialogRiddlePopupBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic

class RiddlePopupDialog(
    private val context: Context,
    private val riddles: List<RiddleDetails>
) {

    companion object {
        private const val questID = 7
        private const val questGoal = 2
        private const val answer3 = 3
    }

    private lateinit var binding: DialogRiddlePopupBinding
    private val dialog = Dialog(context)

    fun show() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DialogRiddlePopupBinding.inflate(inflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        setupViews()
        setOnClickListeners()
        dialog.show()
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
            QuestLogic(context).finishedQuestGoal(questID, questGoal)
            dismissDialog()
        }
    }

    private var dismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    private fun dismissDialog() {
        dialog.dismiss()
        dismissListener?.invoke()
    }
}
