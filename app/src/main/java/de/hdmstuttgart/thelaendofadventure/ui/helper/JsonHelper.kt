package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
/**
 * A helper class for reading JSON files.
 *
 * @param context The application context.
 * @param fileName The name of the JSON file to read.
 */
class JsonHelper(private val context: Context, private val fileName: String) {

    private var jsonString: String
    private var jsonObject: JSONObject
    init {
        try {
            jsonString = readJsonFile()
            jsonObject = JSONObject(jsonString)
        } catch (e: IOException) {
            Log.d(TAG, "Error reading JSON file: ${e.message}")
            jsonString = ""
            jsonObject = JSONObject()
        } catch (e: JSONException) {
            Log.d(TAG, "Error parsing JSON: ${e.message}")
            jsonString = ""
            jsonObject = JSONObject()
        }
    }

    /**
     * Reads the contents of the JSON file and returns it as a String.
     *
     * @return The content of the JSON file.
     */
    private fun readJsonFile(): String {
        val filePath = "conversations/$fileName"
        return try {
            val inputStream = context.assets.open(filePath)
            val buffer = ByteArray(inputStream.available())
            inputStream.use { stream ->
                stream.read(buffer)
            }
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            "File not Found ${e.message}"
        }
    }

    /**
     * Reads the NPC name from a JSON file and returns it.
     *
     * @return the NPC name as a String.
     */
    fun readNpcNameFromJsonFile(): String {
        try {
            return jsonObject.getString("NPC")
        } catch (e: JSONException) {
            Log.d(TAG, "Error parsing JSON: ${e.message}")
        }
        return "No NPC Found"
    }

    /**
     * Reads the NPC img path name from a JSON file and returns it.
     *
     * @return the path to the NPC img as String.
     */
    fun readNpcImgFromJsonFile(): String {
        try {
            return jsonObject.getString("img")
        } catch (e: JSONException) {
            Log.d(TAG, "Error parsing JSON: ${e.message}")
        }
        return "No Img Found"
    }

    /**
     * Reads the dialogue from a JSON file and returns a list of dialogue pairs.
     *
     * @return A list of dialogue pairs (speaker, message).
     */
    fun readDialogueFromJsonFile(): List<Pair<String, String>> {
        val dialogueList = mutableListOf<Pair<String, String>>()
        try {
            val dialogueArray = jsonObject.getJSONArray("dialogue")

            for (i in 0 until dialogueArray.length()) {
                val dialogueObj = dialogueArray.getJSONObject(i)
                val speaker = dialogueObj.getString("speaker")
                val message = dialogueObj.getString("message")
                dialogueList.add(speaker to message)
            }
        } catch (e: JSONException) {
            Log.d(TAG, "Error parsing JSON: ${e.message}")
        }
        return dialogueList
    }

    companion object {
        private const val TAG = "JsonHelper"
    }
}
