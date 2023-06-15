package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class JsonHelperTest {
    private var context: Context? = null
    private var jsonHelper: JsonHelper? = null
    private var negativeJsonHelper: JsonHelper? = null

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        jsonHelper = JsonHelper(context!!, "test.json")
        negativeJsonHelper = JsonHelper(context!!, "negativeTest.json")
    }

    @Test
    fun readNpcNameFromJsonFile_ExistingNpcName_ReturnsNpcName() {
        val npcName = jsonHelper!!.readNpcNameFromJsonFile()
        assertEquals("Expected NPC name", "Reihner Zufall", npcName)
    }

    @Test
    fun readNpcNameFromJsonFile_NonExistingNpcName_ReturnsNoNpcFound() {
        val npcName = negativeJsonHelper!!.readNpcNameFromJsonFile()
        assertEquals("Expected No NPC Found", "No NPC Found", npcName)
    }

    @Test
    fun readNpcImgFromJsonFile_ExistingNpcImg_ReturnsNpcImgPath() {
        val npcImg = jsonHelper!!.readNpcImgFromJsonFile()
        assertEquals("Expected NPC image path", "chat_icon", npcImg)
    }

    @Test
    fun readNpcImgFromJsonFile_NonExistingNpcImg_ReturnsNoImgFound() {
        val npcImg = negativeJsonHelper!!.readNpcImgFromJsonFile()
        assertEquals("Expected No Img Found", "No Img Found", npcImg)
    }

    @Test
    fun readDialogueFromJsonFile_ExistingDialogue_ReturnsDialogueList() {
        val dialogueList = jsonHelper!!.readDialogueFromJsonFile()
        assertEquals("Expected speaker", "Player", dialogueList[2].first)
        assertEquals("Expected message", "Unmöglich!", dialogueList[2].second)
        assertEquals("Expected speaker", "Reihner Zufall", dialogueList[3].first)
        assertEquals("Expected message", "Zu schwach!", dialogueList[3].second)
    }

    @Test
    fun readDialogueFromJsonFile_NonExistingDialogue_ReturnsEmptyList() {
        val dialogueList = negativeJsonHelper!!.readDialogueFromJsonFile()
        assertEquals("Expected empty dialogue list", 0, dialogueList.size)
    }
}
