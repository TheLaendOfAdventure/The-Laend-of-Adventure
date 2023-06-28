package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.content.Context
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
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.DialogAcceptQuestPopupBinding
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.logic.TrackingLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val userID = SharedPreferencesHelper.getUserID(context)

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
                try {
                    for (i in pointAnnotationList.indices) {
                        val pointAnnotation = pointAnnotationList[i]
                        val viewAnnotation = viewList[i]
                        if (pointAnnotation == clickedAnnotation) {
                            viewAnnotation.toggleViewVisibility()
                        }
                    }
                } catch (@Suppress("TooGenericExceptionCaught")e: IndexOutOfBoundsException) {
                    Log.d(TAG, "Alredy deleted $e")
                }
                true
            }
        }
        setUpCompassImage()
        setUpLocationPuck()
    }

    private fun setUpCompassImage() {
        mapview.compass.image = AppCompatResources.getDrawable(context, R.drawable.compass)
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

    @SuppressLint("DiscouragedApi")
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
        val json = JsonHelper(context, quest.dialogPath)
        val npcName = json.readNpcNameFromJsonFile()
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
                TrackingLogic(context).isUserAtQuestLocation(questID) { isMatchingLocation ->
                    if (isMatchingLocation) {
                        val questLogic = QuestLogic(context)
                        questLogic.finishedQuestGoal(questID, START_GOAL)
                        viewAnnotationManager.removeViewAnnotation(viewAnnotation)
                        pointAnnotationManager.delete(pointAnnotation)
                    } else {
                        TrackingLogic(context).notifyTooFarFromQuest(questID)
                    }
                }
            }
        }
    }

    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    companion object {
        private const val TAG = "MapHelper"
        private const val START_GOAL = 0
    }
}
