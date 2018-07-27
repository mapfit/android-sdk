package com.mapfit.android

import com.mapfit.android.annotations.Building
import com.mapfit.android.annotations.BuildingOptions
import com.mapfit.android.annotations.JoinType
import com.mapfit.android.geometry.LatLng
import com.mapfit.tetragon.SceneUpdate
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*

internal class BuildingExtruder(
    val mapController: MapController
) {

    private val buildings = ArrayList<Building>()
    private val extrudeQueue = ArrayDeque<Building>()
    private val flattenQueue = ArrayDeque<Building>()
    private var extrudedBuildingCount = 0
    private var flattenedBuildingCount = 0
    private val stringBuilder = StringBuilder()
    private val styleSceneUpdates = mutableListOf<SceneUpdate>()

    fun flatten(vararg latLngs: LatLng) = launch {
        val alreadyExtruded = mutableListOf<Building>()

        latLngs.forEach { latLng ->
            buildings.firstOrNull { it.latLng == latLng }?.let { alreadyExtruded.add(it) }
        }

        if (alreadyExtruded.isEmpty()) {
            addToQueue(latLngs, flattenQueue)

        } else {
            alreadyExtruded.forEach { buildings.remove(it) }
            refreshStringBuilder()
            updateScene(getFilterFunction())
        }
    }

    fun extrude(vararg latLngs: LatLng, buildingOptions: BuildingOptions) = launch {
        addToQueue(latLngs, extrudeQueue)

        styleSceneUpdates.clear()
        styleSceneUpdates.addAll(bundleSceneUpdates(buildingOptions))
    }

    private fun bundleSceneUpdates(buildingOptions: BuildingOptions): MutableList<SceneUpdate> {
        val styleSceneUpdates = mutableListOf<SceneUpdate>()

        if (buildingOptions.fillColor.isNotBlank()) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-extruded-building-overlay.color",
                    "'${buildingOptions.fillColor}'"
                )
            )
        }
        if (buildingOptions.drawOrder != 600) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-extruded-building-overlay.order",
                    "${buildingOptions.drawOrder}"
                )
            )
        }
        if (buildingOptions.strokeWidth != Integer.MIN_VALUE) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.width",
                    "${buildingOptions.strokeWidth}"
                )
            )
        }
        if (buildingOptions.strokeColor.isNotBlank()) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.color",
                    "'${buildingOptions.strokeColor}'"
                )
            )
        }
        if (buildingOptions.lineJoinType != JoinType.MITER) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.join",
                    "${buildingOptions.lineJoinType}"
                )
            )
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.outline.join",
                    "${buildingOptions.lineJoinType}"
                )
            )
        }
        if (buildingOptions.strokeOutlineColor.isNotBlank()) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.outline.color",
                    "'${buildingOptions.strokeOutlineColor}'"
                )
            )
        }
        if (buildingOptions.strokeOutlineWidth != Integer.MIN_VALUE) {
            styleSceneUpdates.add(
                SceneUpdate(
                    "layers.buildings_extruded.draw.sdk-line-overlay.outline.width",
                    "${buildingOptions.strokeOutlineWidth}"
                )
            )
        }

        return styleSceneUpdates
    }

    private fun addToQueue(
        latLngs: Array<out LatLng>,
        queue: ArrayDeque<Building>
    ) {
        for (latLng in latLngs) {
            val screenPosition = mapController.latLngToScreenPosition(latLng)
            queue.add(Building(screenPosition, latLng = latLng))
            mapController.pickFeature(screenPosition.x, screenPosition.y)
        }

        clearQueueAfterDelay()
    }

    fun handleFeature(properties: MutableMap<String, String>) {
        if (flattenQueue.isNotEmpty()) {
            handleFlattening(properties)
        } else if (extrudeQueue.isNotEmpty()) {
            handleExtruding(properties)
        }
    }

    private fun clearQueueAfterDelay() = launch {
        val postponeDuration = flattenQueue.size * 100 + extrudeQueue.size * 100
        delay(postponeDuration)
        flattenQueue.clear()
        extrudeQueue.clear()
    }

    private fun handleFlattening(properties: MutableMap<String, String>) {
        val (rootId, id) = extractIds(properties)

        val existingBuilding: Building? =
            buildings.firstOrNull {
                (it.id != 0L && it.id == id)
                        || (it.rootId != 0L && it.rootId == rootId)
            }

        existingBuilding?.let {
            buildings.remove(existingBuilding)
            flattenedBuildingCount++
        }

        flattenQueue.remove()

        if (flattenQueue.isEmpty() && flattenedBuildingCount > 0) {
            refreshStringBuilder()
            updateScene(getFilterFunction())
        }

    }

    private fun handleExtruding(properties: MutableMap<String, String>) {
        val building = extrudeQueue.remove()
        addBuildingToExtrude(properties, building)

        if (extrudeQueue.isEmpty() && extrudedBuildingCount > 0) {
            updateScene(getFilterFunction())
        }
    }

    private fun refreshStringBuilder() {
        stringBuilder.setLength(0)

        buildings.forEach {
            if (it.id != 0L) {
                appendId(it.id)
            }

            if (it.rootId != 0L) {
                appendRootId(it.rootId)
            }
        }
    }

    private fun updateScene(filterFunction: String) {
        val sceneUpdates = mutableListOf(
            SceneUpdate(
                "layers.buildings_extruded.filter",
                "function() { return $filterFunction; }"
            )
        )

        if (styleSceneUpdates.size > 0) {
            sceneUpdates.addAll(styleSceneUpdates)
        }

        mapController.updateSceneAsync(sceneUpdates)

        extrudedBuildingCount = 0
        flattenedBuildingCount = 0
    }

    private fun getFilterFunction(): String {
        var function = stringBuilder.toString()
        if (function.startsWith(" || ")) {
            function = function.substring(4)
        }
        return function
    }

    private fun addBuildingToExtrude(properties: Map<String, String>, building: Building) {
        val (rootId, id) = extractIds(properties)

        var buildingExist = false

        // check if the building is already extruded
        for ((_, _, id1, rootId1) in buildings) {
            if (id1 != 0L && id1 == id || rootId1 != 0L && rootId1 == rootId) {
                buildingExist = true
            }
        }

        if (buildingExist) {
            return
        }

        if (rootId != 0L) {
            appendRootId(rootId)
            building.rootId = rootId
        }

        if (id != 0L) {
            appendId(id)
            building.id = id
        }

        buildings.add(building)
        extrudedBuildingCount++
    }

    private fun appendId(id: Long) {
        stringBuilder.append(" || ").append("feature.id == ").append(id)
    }

    private fun appendRootId(rootId: Long) {
        stringBuilder.append(" || ").append("feature.root_id ==").append(rootId)
    }

    private fun extractIds(properties: Map<String, String>): Pair<Long, Long> {
        var rootId = 0L
        var id = 0L

        if (properties.containsKey("root_id")) {
            rootId = java.lang.Long.parseLong(properties["root_id"])

        } else if (properties.containsKey("id")) {
            id = java.lang.Long.parseLong(properties["id"])
        }
        return Pair(rootId, id)
    }

}