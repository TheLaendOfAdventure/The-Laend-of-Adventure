package de.hdmstuttgart.thelaendofadventure.ui.dialogpopup
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import de.hdmstuttgart.the_laend_of_adventure.databinding.ConversationPopupBinding

class ConversationPopupDialog(private val context: Context, private val dialogPath: String) {

    private lateinit var dialog: Dialog

    fun show() {
        dialog = Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ConversationPopupBinding.inflate(inflater)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        binding.partnerTextView.text = "Dennis"
        binding.questCardView.setOnClickListener {
            dismissDialog()
        }
        dialog.show()
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
