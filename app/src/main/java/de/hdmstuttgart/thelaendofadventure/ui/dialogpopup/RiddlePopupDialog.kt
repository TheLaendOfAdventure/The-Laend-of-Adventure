package de.hdmstuttgart.thelaendofadventure.ui.dialogpopup

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import de.hdmstuttgart.the_laend_of_adventure.databinding.DialogRiddlePopupBinding
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.ui.adapters.RiddleAnswerAdapter

class RiddlePopupDialog(
    private val context: Context,
    private val riddles: List<RiddleDetails>
) {

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
        dialog.show()
    }

    private fun setupViews() {
        binding.riddleTextView.text = riddles[0].question
        val adapter = RiddleAnswerAdapter(
            riddles,
            object : RiddleAnswerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if (riddles[position].answer == riddles[position].possibleAnswers) {
                        QuestLogic(context).finishedQuestGoal(
                            riddles[position].questID,
                            riddles[position].goalNumber
                        )
                        dismissDialog()
                    } else {
                        // @todo handle wrong answer
                    }
                }
            }
        )
        binding.answerList.adapter = adapter
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
