package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
/**
 * A helper class for reading JSON files.
 */
class JsonHelper(private val context: Context, private val fileName: String) {
    /**
     * Reads the NPC from a JSON file and returns it.
     *
     * @return the NPC name as a String.
     */
    fun readNpcNameFromJsonFile(): String {
        val jsonString: String? = try {
            // Open the JSON file from the assets folder
            val filePath = "conversations/$fileName"
            val inputStream = context.applicationContext.assets.open(filePath)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            // Convert the byte array to a String using UTF-8 encoding
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            Log.d(TAG, "Conversation File does not exist ${e.message}")
            null
        }
        jsonString?.let {
            try {
                val jsonObject = JSONObject(it)
                return jsonObject.getString("NPC")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return "None NPC Found"
    }

    /**
     * Reads the dialogue from a JSON file and returns a list of dialogue pairs.
     *
     * @return A list of dialogue pairs (speaker, message).
     */
    fun readDialogueFromJsonFile(): List<Pair<String, String>> {
        val dialogueList = mutableListOf<Pair<String, String>>()
        try {
            val filePath = "conversations/$fileName"
            val inputStream = context.assets.open(filePath)
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

    companion object {
        private const val TAG = "JsonHelper"
    }
}
