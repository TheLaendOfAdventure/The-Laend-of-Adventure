package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.Location
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity
import de.hdmstuttgart.thelaendofadventure.logic.QuestLogic
import de.hdmstuttgart.thelaendofadventure.logic.TrackingLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapHelper(
    private val mapview: MapView,
    questList: List<QuestEntity>,
    private val context: Context,
    private val userLevel: Int

) {
    private val pointAnnotationManager: PointAnnotationManager =
        mapview.annotations.createPointAnnotationManager()
    private var iconBitmap: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.chat_icon)?.toBitmap()!!
    private var redMarker: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.banner)?.toBitmap()!!
    private var blankImg =
        AppCompatResources.getDrawable(context, R.drawable.img_blank)?.toBitmap()!!
    private val viewAnnotationManager = mapview.viewAnnotationManager
    val userID = SharedPreferencesHelper.getUserID(context)
    private var annotationList: HashMap<String, PointAnnotation> = hashMapOf()

    private val filteredQuestList = questList.filter { quest ->
        quest.level <= userLevel
    }

    fun setUpMap() {
        mapview.getMapboxMap().loadStyleUri(
            "asset://map/style.json"
        ) {
            val pointAnnotationList = prepareAnnotationMarker()
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
                } catch (@Suppress("TooGenericExceptionCaught") e: IndexOutOfBoundsException) {
                    Log.d(TAG, "Alredy deleted $e")
                }
                true
            }
        }
        setUpCompassImage()
        setUpLocationPuck()
        locationMarkers.observeForever(observer)
    }

    private val observer: Observer<HashMap<String, Location>> = Observer { newMap ->
        val removedEntries: HashMap<String, Location> =
            previousMap.filterKeys { !newMap.containsKey(it) } as HashMap<String, Location>
        val addedEntries: HashMap<String, Location> =
            newMap.filterKeys { !previousMap.containsKey(it) } as HashMap<String, Location>
        Log.d(TAG, "previous list: $previousMap")
        if (removedEntries.isNotEmpty()) {
            removeAnnotationMarker(removedEntries)
            Log.d(TAG, "These entries have been removed: $removedEntries")
        }

        if (addedEntries.isNotEmpty()) {
            addAnnotationMarker(addedEntries)
            Log.d(TAG, "These entries have been added: $addedEntries")
        }

        previousMap = newMap
        Log.d(TAG, " The annotationMarkerList has been updated: $newMap")
    }

    private fun addAnnotationMarker(addedEntries: HashMap<String, Location>) {
        val pointAnnotationOptionsList = getMarkerOptionsList(addedEntries)
        pointAnnotationOptionsList.forEach { (key, pointAnnotationOptions) ->
            val createdAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
            annotationList[key] = createdAnnotation
        }
    }

    private fun getMarkerOptionsList(addedEntries: HashMap<String, Location>): HashMap<String, PointAnnotationOptions> {
        val pointAnnotationOptionsMap: HashMap<String, PointAnnotationOptions> = hashMapOf()

        addedEntries.forEach { (key, location) ->
            val point = Point.fromLngLat(location.longitude, location.latitude)

            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(redMarker)
                .withDraggable(false)

            pointAnnotationOptionsMap[key] = pointAnnotationOptions
        }
        return pointAnnotationOptionsMap
    }

    private fun removeAnnotationMarker(markerHashMap: HashMap<String, Location>) {
        markerHashMap.forEach { (key, _) ->
            val marker = annotationList[key]
            marker!!.iconImageBitmap = iconBitmap
            pointAnnotationManager.delete(marker)
            annotationList.remove(key)
        }
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

    private fun prepareAnnotationMarker(): List<PointAnnotation> {
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
                return@mapIndexed viewAnnotation
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
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                val questLogic = QuestLogic(context)
                                questLogic.finishedQuestGoal(questID, START_GOAL)
                            }
                            Log.d(TAG, "After withContext (FinishQuestLogic)")
                            updateViewAndAnnotation(viewAnnotation, pointAnnotation)
                        }
                    }
                }
            }
        }
    }

    private fun updateViewAndAnnotation(
        viewAnnotation: View,
        pointAnnotation: PointAnnotation
    ) {
        viewAnnotationManager.removeViewAnnotation(viewAnnotation)
        pointAnnotationManager.delete(pointAnnotation)
        Log.d(TAG, "$pointAnnotation got deleted")
        pointAnnotation.iconImageBitmap = blankImg
        pointAnnotationManager.update(pointAnnotation)
        Log.d(TAG, Thread.currentThread().name)
    }

    fun stopObservingLocationMarkers() {
        locationMarkers.removeObserver(observer)
    }

    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    companion object {
        var locationMarkers = MutableLiveData<HashMap<String, Location>>().apply {
            postValue(HashMap()) // Initialize with an empty HashMap
        }
        var previousMap: Map<String, Location> = mapOf()

        private const val TAG = "MapHelper"
        private const val START_GOAL = 0
    }
}
