package de.hdmstuttgart.thelaendofadventure.ui.dialogpopup
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import de.hdmstuttgart.the_laend_of_adventure.databinding.DialogConversationPopupBinding

class ConversationPopupDialog(private val context: Context, private val dialogPath: String) {

    private lateinit var dialog: Dialog

    fun show() {
        dialog = Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DialogConversationPopupBinding.inflate(inflater)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        binding.partnerTextView.text = "Dennis"
        binding.dialogCard.setOnClickListener {
            dismissDialog()
        }
        dialog.show()
    }

    private var dismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        println(dialogPath)
        dismissListener = listener
    }

    private fun dismissDialog() {
        dialog.dismiss()
        dismissListener?.invoke()
    }
}