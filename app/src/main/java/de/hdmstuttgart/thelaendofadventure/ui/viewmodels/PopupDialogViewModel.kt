/*
package de.hdmstuttgart.thelaendofadventure.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class PopupDialogViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "PopupDialogViewModel"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var questName: String = ""
    var questDescription:String = ""
    var npcName = ""
    private val questRepository: QuestRepository = AppDataContainer(application).questRepository
    val userID = application.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE,
    ).getInt(R.string.userID.toString(), -1)

    val quests = questRepository.getUnacceptedQuestsByUserID(userID).asLiveData(Dispatchers.Main)

    fun createPopup(questID: Int){

        npcName = readNpcNameFromJsonFile()
    }

    fun readNpcNameFromJsonFile(filePath: String): String {
        val applicationContext = getApplication<Application>().applicationContext
        val jsonString: String? = try {
            // Open the JSON file from the assets folder
            val inputStream: InputStream = applicationContext.assets.open(filePath)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            // Convert the byte array to a String using UTF-8 encoding
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            Log.e(TAG, "File does not Exist")
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
}
*/
