package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * This class contains unit tests for the JsonHelper class.
 */
class JsonHelperTest {

    private var context: Context? = null
    private var jsonHelper: JsonHelper? = null
    private var negativeJsonHelper: JsonHelper? = null
    private var nonExistingJsonHelper: JsonHelper? = null

    /**
     * Sets up the necessary objects.
     */
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        jsonHelper = JsonHelper(context!!, "test.json")
        negativeJsonHelper = JsonHelper(context!!, "negativeTest.json")
        nonExistingJsonHelper = JsonHelper(context!!, "nonExistingFile.json")
    }

    /**
     * Tests the readNpcNameFromJsonFile() method when an existing NPC name is returned.
     * It asserts that the expected NPC name is equal to the returned NPC name.
     */
    @Test
    fun readNpcNameFromJsonFile_ExistingNpcName_ReturnsNpcName() {
        val npcName = jsonHelper!!.readNpcNameFromJsonFile()
        assertEquals("Expected NPC name", "Reihner Zufall", npcName)
    }

    /**
     * Tests the readNpcNameFromJsonFile() method when a non-existing NPC name is returned.
     * It asserts that the returned NPC name is "No NPC Found".
     */
    @Test
    fun readNpcNameFromJsonFile_NonExistingNpcName_ReturnsNoNpcFound() {
        val npcName = negativeJsonHelper!!.readNpcNameFromJsonFile()
        assertEquals("Expected No NPC Found", "No NPC Found", npcName)
    }

    /**
     * Tests the readNpcImgFromJsonFile() method when an existing NPC image path is returned.
     * It asserts that the expected NPC image path is equal to the returned NPC image path.
     */
    @Test
    fun readNpcImgFromJsonFile_ExistingNpcImg_ReturnsNpcImgPath() {
        val npcImg = jsonHelper!!.readNpcImgFromJsonFile()
        val expectedNpcImg = "[(Reihner Zufall, chat_icon), (Johannes, compass)]"
        assertEquals("Expected NPC image path", expectedNpcImg, npcImg.toString())
    }

    /**
     * Tests the readNpcImgFromJsonFile() method when a non-existing NPC image path is returned.
     * It asserts that the returned NPC image path is "No Img Found".
     */
    @Test
    fun readNpcImgFromJsonFile_NonExistingNpcImg_ReturnsNoImgFound() {
        val npcImg = negativeJsonHelper!!.readNpcImgFromJsonFile()
        val expectedNpcImg = emptyList<Pair<String, String>>()
        assertEquals("Expected No Img Found", expectedNpcImg, npcImg)
    }

    /**
     * Tests the readDialogueFromJsonFile() method when an existing dialogue is returned.
     * It asserts that the expected speaker and message are equal to the values in the dialogue list.
     */
    @Test
    fun readDialogueFromJsonFile_ExistingDialogue_ReturnsDialogueList() {
        val dialogueList = jsonHelper!!.readDialogueFromJsonFile()
        assertEquals("Expected speaker", "Player", dialogueList[2].first)
        assertEquals("Expected message", "Unmöglich!", dialogueList[2].second)
        assertEquals("Expected speaker", "Reihner Zufall", dialogueList[3].first)
        assertEquals("Expected message", "Zu schwach!", dialogueList[3].second)
    }

    /**
     * Tests the readDialogueFromJsonFile() method when a non-existing dialogue is returned.
     * It asserts that the returned dialogue list is empty.
     */
    @Test
    fun readDialogueFromJsonFile_NonExistingDialogue_ReturnsEmptyList() {
        val dialogueList = negativeJsonHelper!!.readDialogueFromJsonFile()
        assertEquals("Expected empty dialogue list", 0, dialogueList.size)
    }

    /**
     * Tests the readNpcNameFromJsonFile() method when a non-existing file is provided.
     * It asserts that the returned NPC name is "No NPC Found".
     */
    @Test
    fun readNpcNameFromJsonFile_NonExistingFile_ReturnsNoNPCFound() {
        val npcName = nonExistingJsonHelper?.readNpcNameFromJsonFile()
        assertEquals("Expected No NPC Found", "No NPC Found", npcName)
    }

    /**
     * Tests the readNpcImgFromJsonFile() method when a non-existing file is provided.
     * It asserts that the returned image path is "[]".
     */
    @Test
    fun readNpcImgFromJsonFile_NonExistingFile_ReturnsNoImgFound() {
        val img = nonExistingJsonHelper?.readNpcImgFromJsonFile()
        val expectedNpcImg = emptyList<Pair<String, String>>()
        assertEquals("Expected empty List", expectedNpcImg, img)
    }

    /**
     * Tests the readDialogueFromJsonFile() method when a non-existing file is provided.
     * It asserts that the returned dialogue list is empty.
     */
    @Test
    fun readDialogueFrom_NonExistingFile_ReturnsDialogueList() {
        val dialogueList = nonExistingJsonHelper?.readDialogueFromJsonFile()
        assertEquals("Expected Empty List", 0, dialogueList?.size)
    }
}
