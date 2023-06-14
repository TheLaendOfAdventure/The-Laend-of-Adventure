package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context // ktlint-disable import-ordering
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxViewAnnotationException
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.DialogAcceptQuestPopupBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MapHelper(
    private val mapview: MapView,
    questList: List<QuestEntity>,
    private val context: Context,
    private val userLevel: Int

) {
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var iconBitmap: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.chat_icon)?.toBitmap()!!
    private val viewAnnotationManager = mapview.viewAnnotationManager
    val userID = context.getSharedPreferences(
        R.string.sharedPreferences.toString(),
        Context.MODE_PRIVATE
    ).getInt(R.string.userID.toString(), -1)

    private val filteredQuestList = questList.filter { quest ->
        quest.level <= userLevel
    }

    fun setUpMap() {
        mapview.getMapboxMap().loadStyleUri(
            context.getString(R.string.mapbox_styleURL)
        ) {
            val pointAnnotationList = prepareAnnotationMarker(mapview)
            val viewList = prepareViewAnnotation(pointAnnotationList, filteredQuestList)
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
        setUpLocationPuck()
    }

    private fun setUpLocationPuck() {
        mapview.location.locationPuck = LocationPuck2D(
            topImage = AppCompatResources.getDrawable(
                context,
                R.drawable.location_puck
            )
        )
    }

    private fun prepareAnnotationMarker(
        mapView: MapView
    ): List<PointAnnotation> {
        val annotationPlugin = mapView.annotations
        pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        val pointAnnotationOptionsList = getPointAnnotationOptionsList(filteredQuestList)
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
                    R.layout.dialog_accept_quest_popup,
                    options
                )

                viewAnnotation.visibility = View.GONE

                questViewBinding(questList[index], viewAnnotation, pointAnnotation)
                viewAnnotation
            }
            return viewAnnotationList
        } catch (e: MapboxViewAnnotationException) {
            Log.d(TAG, "View Already existing $e")
        }

        return emptyList()
    }

    private fun questViewBinding(
        quest: QuestEntity,
        viewAnnotation: View,
        pointAnnotation: PointAnnotation
    ) {
        val binding = DialogAcceptQuestPopupBinding.bind(viewAnnotation)
        val imageName = quest.imagePath ?: ""
        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        binding.dialogAcceptQuestImage.setImageResource(resourceId)
        binding.dialogAcceptQuestName.text = quest.name
        val npcName = readNpcNameFromJsonFile(quest.dialogPath)
        binding.dialogAcceptQuestQuestDescription.text = context.getString(
            R.string.npc_name,
            npcName
        )
        binding.dialogAcceptQuestQuestDetails.text = context.getString(
            R.string.quest_details,
            quest.latitude,
            quest.longitude,
            quest.description
        )

        binding.dialogAcceptQuestAcceptButton.text = context.getString(R.string.quest_accept)
        binding.dialogAcceptQuestDeclineButton.text = context.getString(R.string.quest_decline)

        configureViewAnnotationButtons(viewAnnotation, quest.questID, pointAnnotation)
    }
    private fun configureViewAnnotationButtons(
        viewAnnotation: View,
        questID: Int,
        pointAnnotation: PointAnnotation
    ) {
        val binding = DialogAcceptQuestPopupBinding.bind(viewAnnotation)
        binding.dialogAcceptQuestDeclineButton.setOnClickListener {
            viewAnnotation.visibility = View.GONE
        }

        binding.dialogAcceptQuestAcceptButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                QuestLogic(context).finishedQuestGoal(questID, START_GOAL)
            }
            viewAnnotationManager.removeViewAnnotation(viewAnnotation)
            pointAnnotationManager.delete(pointAnnotation)
        }
    }

    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun readNpcNameFromJsonFile(filePath: String): String {
        val applicationContext = context.applicationContext
        val jsonString: String? = try {
            // Open the JSON file from the assets folder
            val completeFilePath = "conversations/$filePath"
            val inputStream = applicationContext.assets.open(completeFilePath)
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
        private const val START_GOAL = 0
    }
}
