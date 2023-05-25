import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import de.hdmstuttgart.the_laend_of_adventure.databinding.ConversationPopupBinding

class ConversationPopupDialog(private val context: Context, private val dialogPath: String) {

    fun show() {
        val dialog = Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ConversationPopupBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        binding.partnerTextView.text = "Dennis"

        dialog.show()
    }
}
