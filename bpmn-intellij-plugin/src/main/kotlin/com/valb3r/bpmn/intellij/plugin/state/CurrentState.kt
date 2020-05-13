package com.valb3r.bpmn.intellij.plugin.state

import com.valb3r.bpmn.intellij.plugin.bpmn.api.BpmnProcessObjectView
import com.valb3r.bpmn.intellij.plugin.bpmn.api.bpmn.BpmnElementId
import com.valb3r.bpmn.intellij.plugin.bpmn.api.bpmn.elements.WithBpmnId
import com.valb3r.bpmn.intellij.plugin.bpmn.api.diagram.DiagramElementId
import com.valb3r.bpmn.intellij.plugin.bpmn.api.diagram.elements.ShapeElement
import com.valb3r.bpmn.intellij.plugin.bpmn.api.info.Property
import com.valb3r.bpmn.intellij.plugin.bpmn.api.info.PropertyType
import com.valb3r.bpmn.intellij.plugin.events.StringValueUpdatedEvent
import com.valb3r.bpmn.intellij.plugin.events.updateEventsRegistry
import com.valb3r.bpmn.intellij.plugin.render.EdgeElementState
import com.valb3r.bpmn.intellij.plugin.render.WaypointElementState
import java.util.concurrent.atomic.AtomicReference

private val currentStateProvider = AtomicReference<CurrentStateProvider>()

fun currentStateProvider(): CurrentStateProvider {
    return currentStateProvider.updateAndGet {
        if (null == it) {
            return@updateAndGet CurrentStateProvider()
        }

        return@updateAndGet it
    }
}

data class CurrentState(
        val shapes: List<ShapeElement>,
        val edges: List<EdgeElementState>,
        val elementByDiagramId: Map<DiagramElementId, BpmnElementId>,
        val elementByStaticId: Map<BpmnElementId, WithBpmnId>,
        val elemPropertiesByStaticElementId: Map<BpmnElementId, Map<PropertyType, Property>>
)

// Global singleton
class CurrentStateProvider {

    private val updateEvents = updateEventsRegistry()
    private var fileState = CurrentState(emptyList(), emptyList(), emptyMap(), emptyMap(), emptyMap())
    private var currentState = CurrentState(emptyList(), emptyList(), emptyMap(), emptyMap(), emptyMap())

    fun resetStateTo(processObject: BpmnProcessObjectView) {
        fileState = CurrentState(
                processObject.diagram.firstOrNull()?.bpmnPlane?.bpmnShape ?: emptyList(),
                processObject.diagram.firstOrNull()?.bpmnPlane?.bpmnEdge?.map { EdgeElementState(it) } ?: emptyList(),
                processObject.elementByDiagramId,
                processObject.elementByStaticId,
                processObject.elemPropertiesByElementId
        )
        currentState = fileState
        updateEvents.reset()
    }

    fun currentState(): CurrentState {
        val newShapes = updateEvents.newShapeElements().map { it.event }
        val newEdges = updateEvents.newEdgeElements().map { it.event }
        val newEdgeElems = updateEvents.newEdgeElements().map { it.event }

        val newElementByDiagramId: MutableMap<DiagramElementId, BpmnElementId> = HashMap()
        val newElementByStaticId: MutableMap<BpmnElementId, WithBpmnId> = HashMap()
        val newElemPropertiesByStaticElementId: MutableMap<BpmnElementId, Map<PropertyType, Property>> = HashMap()

        newShapes.forEach {newElementByDiagramId[it.shape.id] = it.shape.bpmnElement}
        newEdgeElems.forEach {newElementByDiagramId[it.edge.id] = it.bpmnObject.id}

        newShapes.forEach {newElementByStaticId[it.bpmnObject.id] = it.bpmnObject}
        newEdges.forEach {newElementByStaticId[it.bpmnObject.id] = it.bpmnObject}

        newShapes.forEach {newElemPropertiesByStaticElementId[it.bpmnObject.id] = it.props}
        newEdges.forEach {newElemPropertiesByStaticElementId[it.bpmnObject.id] = it.props}

        // Update element names
        val allProperties = fileState.elemPropertiesByStaticElementId.toMutableMap().plus(newElemPropertiesByStaticElementId)
        val updatedProperties:  MutableMap<BpmnElementId, MutableMap<PropertyType, Property>> = HashMap()
        allProperties.forEach { prop ->
            updatedProperties[prop.key] = prop.value.toMutableMap()
            updateEvents.currentPropertyUpdateEventList(prop.key)
                    .map { it.event }
                    .filterIsInstance<StringValueUpdatedEvent>()
                    .forEach {
                        updatedProperties[prop.key]?.set(it.property, Property(it.newValue))
                    }
        }

        return CurrentState(
                shapes = fileState.shapes.toList().union(newShapes.map { it.shape })
                        .filter { !updateEvents.isDeleted(it.id) && !updateEvents.isDeleted(it.bpmnElement) }
                        .map { updateLocationAndInnerTopology(it) },
                edges = fileState.edges.toList().union(newEdgeElems.map { it.edge })
                        .filter { !updateEvents.isDeleted(it.id) && !(it.bpmnElement?.let { updateEvents.isDeleted(it) }  ?: false)}
                        .map { updateLocationAndInnerTopology(it) },
                elementByDiagramId = fileState.elementByDiagramId.toMutableMap().plus(newElementByDiagramId),
                elementByStaticId = fileState.elementByStaticId.toMutableMap().plus(newElementByStaticId),
                elemPropertiesByStaticElementId = updatedProperties
        )
    }

    private fun updateLocationAndInnerTopology(elem: ShapeElement): ShapeElement {
        val updates = updateEvents.currentLocationUpdateEventList(elem.id)
        var dx = 0.0f
        var dy = 0.0f
        updates.forEach { dx += it.event.dx; dy += it.event.dy }
        return elem.copyAndTranslate(dx, dy)
    }

    private fun updateLocationAndInnerTopology(elem: EdgeElementState): EdgeElementState {
        val hasNoCommittedAnchorUpdates = elem.waypoint.firstOrNull { updateEvents.currentLocationUpdateEventList(it.id).isNotEmpty() }
        val hasNoNewAnchors = updateEvents.newWaypointStructure(elem.id).isEmpty()
        if (null == hasNoCommittedAnchorUpdates && hasNoNewAnchors) {
            return elem
        }

        val waypoints: MutableList<WaypointElementState> =
                updateEvents.newWaypointStructure(elem.id).lastOrNull()?.event?.waypoints?.toMutableList() ?: elem.waypoint.filter { it.physical }.toMutableList()
        val updatedLocations = waypoints.filter { it.physical }.map { updateWaypointLocation(it) }
        return EdgeElementState(elem, updatedLocations)
    }

    private fun updateWaypointLocation(waypoint: WaypointElementState): WaypointElementState {
        val updates = updateEvents.currentLocationUpdateEventList(waypoint.id)
        var dx = 0.0f
        var dy = 0.0f
        updates.forEach { dx += it.event.dx; dy += it.event.dy }

        return waypoint.copyAndTranslate(dx, dy)
    }
}