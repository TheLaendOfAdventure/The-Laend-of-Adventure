
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import de.hdmstuttgart.the_laend_of_adventure.databinding.ConversationPopupBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ConversationPopupDialog(private val context: Context, private val dialogPath: String) {
    private lateinit var binding: ConversationPopupBinding
    fun show() {
        val dialog = Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ConversationPopupBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        val dialogueList = readDialogueFromJsonFile(context, dialogPath)
        displayDialogue(dialogueList)
        dialog.show()
    }

    private fun readDialogueFromJsonFile(context: Context, dialogPath: String):
        List<Pair<String, String>> {
        val dialogueList = mutableListOf<Pair<String, String>>()
        try {
            val inputStream = context.assets.open(dialogPath)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val jsonString = String(buffer, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonString)
            val dialogueArray = jsonObject.getJSONArray("dialogue")

            for (i in 0 until dialogueArray.length()) {
                val dialogueObj = dialogueArray.getJSONObject(i)
                val speaker = dialogueObj.getString("speaker")
                val message = dialogueObj.getString("message")
                dialogueList.add(speaker to message)
            }
        } catch (e: IOException) {
            Log.d(TAG, "$e")
        } catch (e: JSONException) {
            Log.d(TAG, "$e")
        }
        return dialogueList
    }

    private fun displayDialogue(
        dialogueList: List<Pair<String, String>>,
    ) {
        for (dialogue in dialogueList) {
            val speaker = dialogue.first
            val message = dialogue.second

            if (speaker == "Player") {
                binding.userTextbox.visibility = View.VISIBLE
                binding.partnerTextbox.visibility = View.GONE
                binding.userTextView.text = message
            } else {
                binding.userTextbox.visibility = View.GONE
                binding.partnerTextbox.visibility = View.VISIBLE
                binding.partnerTextView.text = message
            }
        }
    }

    companion object {
        private const val TAG = "ConversationPopupDialog"
    }
}
