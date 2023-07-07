package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.annotation.SuppressLint // ktlint-disable import-ordering
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
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

/**
 * Helper class for managing the map and annotations in the UI.
 * @param mapview The MapView instance.
 * @param questList The list of quests.
 * @param context The context.
 * @param userLevel The user level.
 * @param viewLifecycleOwner The LifecycleOwner for observing changes.
 */
@Suppress("TooManyFunctions")
class MapHelper(
    private val mapview: MapView,
    questList: List<QuestEntity>,
    private val context: Context,
    private val userLevel: Int,
    private val viewLifecycleOwner: LifecycleOwner
) {

    /**
     * The [PointAnnotationManager] responsible for managing point annotations on the map.
     */
    private val pointAnnotationManager: PointAnnotationManager =
        mapview.annotations.createPointAnnotationManager()

    /**
     * The icon bitmap for quests.
     */
    private var questIcon: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.scroll)?.toBitmap()!!

    /**
     * The icon bitmap for location markers.
     */
    private var locationMarker: Bitmap =
        AppCompatResources.getDrawable(context, R.drawable.banner)?.toBitmap()!!

    /**
     * The blank image bitmap for replacing other icons.
     */
    private var blankImg =
        AppCompatResources.getDrawable(context, R.drawable.img_blank)?.toBitmap()!!

    /**
     * The [viewAnnotationManager] responsible for managing view annotations on the map.
     */
    private val viewAnnotationManager = mapview.viewAnnotationManager

    /**
     * The ID of the user.
     */
    val userID = SharedPreferencesHelper.getUserID(context)

    /**
     * A map of annotation IDs to [PointAnnotation] objects.
     */
    private var annotationList: HashMap<String, PointAnnotation> = hashMapOf()

    /**
     * The filtered list of quests based on user level.
     */
    private val filteredQuestList = questList.filter { quest ->
        quest.level <= userLevel
    }

    /**
     * Sets up the map by loading the map style, adding annotations, setting up compass, and location puck.
     */
    fun setUpMap() {
        mapview.getMapboxMap().loadStyleUri(
            context.getString(R.string.mapbox_styleURL)
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
                    Log.d(TAG, "Already deleted $e")
                }
                true
            }
        }
        setUpCompassImage()
        setUpLocationPuck()
        addObserver()
    }

    /**
     * Adds an observer to the [locationMarkers] LiveData to track changes in location markers.
     */
    private fun addObserver() {
        if (!locationMarkers.hasActiveObservers()) {
            Log.d(TAG, "Now observing!")
            locationMarkers.observe(viewLifecycleOwner, observer)
        }
    }

    /**
     * Observer for location markers.
     */
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

    /**
     * Adds annotation markers to the map for the given locations.
     * @param addedEntries The map of Markers to add.
     */
    private fun addAnnotationMarker(addedEntries: HashMap<String, Location>) {
        val pointAnnotationOptionsList = getMarkerOptionsList(addedEntries)
        pointAnnotationOptionsList.forEach { (key, pointAnnotationOptions) ->
            val createdAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
            annotationList[key] = createdAnnotation
        }
    }

    /**
     * Creates a list of point annotation options for the given locations.
     * @param addedEntries The map of Markers to add.
     */
    private fun getMarkerOptionsList(addedEntries: HashMap<String, Location>):
        HashMap<String, PointAnnotationOptions> {
        val pointAnnotationOptionsMap: HashMap<String, PointAnnotationOptions> = hashMapOf()

        addedEntries.forEach { (key, location) ->
            val point = Point.fromLngLat(location.longitude, location.latitude)

            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(locationMarker)
                .withDraggable(false)

            pointAnnotationOptionsMap[key] = pointAnnotationOptions
        }
        return pointAnnotationOptionsMap
    }

    /**
     * Removes annotation markers from the map for the given locations.
     * @param markerHashMap The map of Markers to remove.
     */
    private fun removeAnnotationMarker(markerHashMap: HashMap<String, Location>) {
        markerHashMap.forEach { (key, _) ->
            val marker = annotationList[key]
            marker!!.iconImageBitmap = questIcon
            pointAnnotationManager.delete(marker)
            annotationList.remove(key)
        }
    }

    /**
     * Sets up the compass image on the map.
     */
    private fun setUpCompassImage() {
        mapview.compass.image = AppCompatResources.getDrawable(context, R.drawable.compass)
    }

    /**
     * Sets up the location puck on the map.
     */
    private fun setUpLocationPuck() {
        mapview.location.locationPuck = LocationPuck2D(
            topImage = AppCompatResources.getDrawable(
                context,
                R.drawable.location_puck
            )
        )
    }

    /**
     * Prepares and adds point annotations to the map for quests.
     * @return The list of created [PointAnnotation] objects.
     */
    private fun prepareAnnotationMarker(): List<PointAnnotation> {
        val pointAnnotationOptionsList = getPointAnnotationOptionsList(filteredQuestList)
        return pointAnnotationOptionsList.map { pointAnnotationOptions ->
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    /**
     * Creates a list of point annotation options for the given quests.
     * @return The list of created [PointAnnotationOptions] objects.
     */
    private fun getPointAnnotationOptionsList(questList: List<QuestEntity>):
        List<PointAnnotationOptions> {
        return questList.map { quest ->
            val point = Point.fromLngLat(quest.longitude, quest.latitude)

            PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(questIcon)
                .withIconAnchor(IconAnchor.BOTTOM)
                .withDraggable(false)
        }
    }

    /**
     * Prepares and adds view annotations to the map for quests.
     * @param pointAnnotationList The list of point annotations.
     * @param questList The list of quests.
     * @return The list of created [View] objects.
     */
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

    /**
     * Sets up the data binding for the quest view annotation.
     * @param quest The quest entity.
     * @param viewAnnotation The view annotation.
     * @param pointAnnotation The corresponding point annotation.
     */
    @SuppressLint("DiscouragedApi")
    private fun questViewBinding(
        quest: QuestEntity,
        viewAnnotation: View,
        pointAnnotation: PointAnnotation
    ) {
        val binding = DialogAcceptQuestPopupBinding.bind(viewAnnotation)
        binding.dialogAcceptQuestName.text = quest.name
        val json = JsonHelper(context, quest.dialogPath)
        val npcName = json.readNpcNameFromJsonFile()
        binding.dialogAcceptQuestQuestDescription.text = context.getString(
            R.string.npc_name,
            npcName
        )
        binding.dialogAcceptQuestQuestDetails.text = context.getString(
            R.string.quest_details,
            quest.description
        )
        binding.dialogAcceptQuestAcceptButton.text = context.getString(R.string.quest_accept)
        binding.dialogAcceptQuestDeclineButton.text = context.getString(R.string.quest_decline)

        configureViewAnnotationButtons(viewAnnotation, quest.questID, pointAnnotation)
    }

    /**
     * Configures the buttons in the view annotation.
     * @param viewAnnotation The view annotation.
     * @param questID The ID of the quest.
     * @param pointAnnotation The corresponding point annotation.
     */
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
                    } else {
                        TrackingLogic(context).notifyTooFarFromQuest(questID)
                    }
                }
            }
        }
    }

    /**
     * Updates the view annotation and corresponding point annotation after accepting a quest.
     * @param viewAnnotation The view annotation.
     * @param pointAnnotation The corresponding point annotation.
     */
    private fun updateViewAndAnnotation(
        viewAnnotation: View,
        pointAnnotation: PointAnnotation
    ) {
        viewAnnotationManager.removeViewAnnotation(viewAnnotation)
        pointAnnotation.iconImageBitmap = blankImg
        pointAnnotationManager.update(pointAnnotation)
        pointAnnotationManager.delete(pointAnnotation)
        Log.d(TAG, "$pointAnnotation got deleted")
        Log.d(TAG, Thread.currentThread().name)
    }

    /**
     * Stops the Observer.
     */
    fun stopObservingLocationMarkers() {
        locationMarkers.removeObservers(viewLifecycleOwner)
        Log.d(TAG, "locationMarkers are observed: ${locationMarkers.hasActiveObservers()}")
    }

    /**
     * Toggles visibility from GONE to VISIBLE and vice versa.
     */
    private fun View.toggleViewVisibility() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    companion object {
        /**
         * A static Map of location Markers to add.
         */
        var locationMarkers = MutableLiveData<HashMap<String, Location>>().apply {
            postValue(HashMap()) // Initialize with an empty HashMap
        }

        /**
         * A static Map of the location markers from the previous map.
         */
        var previousMap: Map<String, Location> = mapOf()

        private const val TAG = "MapHelper"
        private const val START_GOAL = 0
    }
}
