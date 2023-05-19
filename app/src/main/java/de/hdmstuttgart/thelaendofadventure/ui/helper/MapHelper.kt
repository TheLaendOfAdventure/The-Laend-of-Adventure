package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.PopupDialogBinding
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class MapHelper(
    private val mapview: MapView,
    private val questList: List<QuestEntity>,
    private val context: Context,

) {
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var iconBitmap: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.chat_icon)?.toBitmap()!!
    private val viewAnnotationManager = mapview.viewAnnotationManager
    val userID = context.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)

    fun setUpMap() {
        mapview.getMapboxMap().loadStyleUri(
            context.getString(R.string.mapbox_styleURL)
        ) {
            val pointAnnotationList = prepareAnnotationMarker(mapview)

            val viewList = prepareViewAnnotation(pointAnnotationList, questList)
            // show / hide view annotation based on a marker click
            pointAnnotationManager.addClickListener { clickedAnnotation ->
                pointAnnotationList.forEach { pointAnnotation ->
                    viewList.forEach { viewAnnotation ->
                        if (pointAnnotation == clickedAnnotation) {
                            viewAnnotation.toggleViewVisibility()
                        }
                    }
                }
                true
            }
        }
    }

    private fun prepareAnnotationMarker(
        mapView: MapView,
    ): List<PointAnnotation> {
        val annotationPlugin = mapView.annotations
        pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        val pointAnnotationOptionsList = getPointAnnotationOptionsList(questList)
        return pointAnnotationOptionsList.map { pointAnnotationOptions ->
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun getPointAnnotationOptionsList(questList: List<QuestEntity>):
        List<PointAnnotationOptions> {
        return questList.map { quest ->
            val point = Point.fromLngLat(quest.longitude, quest.latitude)

            PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(iconBitmap)
                .withIconAnchor(IconAnchor.BOTTOM)
                .withDraggable(false)
        }
    }

    private fun prepareViewAnnotation(
        pointAnnotationList: List<PointAnnotation>,
        questList: List<QuestEntity>
    ): List<View> {
        try {
            val viewAnnotationList = pointAnnotationList.mapIndexed { index, pointAnnotation ->
                val options = viewAnnotationOptions {
                    geometry(pointAnnotation.point)
                    associatedFeatureId(pointAnnotation.featureIdentifier)
                    anchor(ViewAnnotationAnchor.BOTTOM)
                    offsetY((pointAnnotation.iconImageBitmap?.height!!).toInt())
                }

                val viewAnnotation = viewAnnotationManager.addViewAnnotation(
                    R.layout.popup_dialog,
                    options
                )

                viewAnnotation.visibility = View.GONE

                questViewBinding(questList[index], viewAnnotation)
                viewAnnotation
            }
            return viewAnnotationList
        } catch (e: Exception) {
            Log.d(TAG, "View Already existing")
        }

        return emptyList()
    }
    private fun questViewBinding(quest: QuestEntity, viewAnnotation: View) {
        val binding = PopupDialogBinding.bind(viewAnnotation)
        binding.dialogPopupImage.setImageURI(quest.imagePath?.toUri())
        binding.popupDialogName.text = quest.name
        val npcName = readNpcNameFromJsonFile(quest.dialogPath)
        binding.popupDialogQuestDescription.text = context.getString(R.string.npc_name, npcName)
        binding.popupDialogQuestDetails.text = context.getString(
            R.string.quest_details,
            quest.latitude,
            quest.longitude,
            quest.description
        )

        binding.popupDialogAcceptButton.text = context.getString(R.string.quest_accept)
        binding.popupDialogDeclineButton.text = context.getString(R.string.quest_decline)

        configureViewAnnotationButtons(viewAnnotation, quest.questID)
    }
    private fun configureViewAnnotationButtons(viewAnnotation: View, questID: Int) {
        val binding = PopupDialogBinding.bind(viewAnnotation)
        binding.popupDialogDeclineButton.setOnClickListener {
            viewAnnotation.visibility = View.GONE
        }

        binding.popupDialogAcceptButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDataContainer(context).questRepository.assignQuestToUser(userID, questID)
            }
            viewAnnotationManager.removeViewAnnotation(viewAnnotation)
        }
    }

    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun readNpcNameFromJsonFile(filePath: String): String {
        val applicationContext = context.applicationContext
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

    companion object {
        private const val TAG = "MapHelper"
    }
}
