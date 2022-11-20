package org.modelix.model.server.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias NodeId = String

@Serializable
data class NodeData(
    val nodeId: NodeId,
    val concept: String?,
    val parent: NodeId?,
    val role: String?,
    val properties: Map<String, String>,
    val references: Map<String, String>,
    val children: Map<String?, List<NodeId>>
)

@Serializable
data class NodeUpdateData(
    val nodeId: NodeId?,
    val temporaryNodeId: NodeId? = null,
    val parent: NodeId? = null,
    val role: String? = null,
    val index: Int? = null, // when a new node is created, this is the position in the parent
    val concept: String? = null,
    val references: Map<String, String?>? = null,
    val properties: Map<String, String?>? = null,
    val children: Map<String?, List<NodeId>>? = null
) {
    fun replaceIds(replacer: (String)->String?): NodeUpdateData {
        val replaceOrKeep: (String)->String = { replacer(it) ?: it }

        val newNodeId = temporaryNodeId?.let(replacer) ?: nodeId
        return NodeUpdateData(
            nodeId = newNodeId,
            temporaryNodeId = if (newNodeId == null) temporaryNodeId else null,
            parent = parent?.let(replaceOrKeep),
            role = role,
            index = index,
            concept = concept,
            references = references?.mapValues { it.value?.let(replaceOrKeep) },
            properties = properties,
            children = children?.mapValues { it.value.map(replaceOrKeep) }
        )
    }

    fun withReference(referenceRole: String, target: NodeId?) = NodeUpdateData(
        nodeId = nodeId,
        temporaryNodeId = temporaryNodeId,
        parent = parent,
        role = role,
        index = index,
        concept = concept,
        references = (references ?: emptyMap()) + (referenceRole to target),
        properties = properties,
        children = children
    )
    fun withProperty(propertyRole: String, value: String?) = NodeUpdateData(
        nodeId = nodeId,
        temporaryNodeId = temporaryNodeId,
        parent = parent,
        role = role,
        index = index,
        concept = concept,
        references = references,
        properties = (properties ?: emptyMap()) + (propertyRole to value),
        children = children
    )
    fun withChildren(childrenRole: String?, newChildren: List<String>) = NodeUpdateData(
        nodeId = nodeId,
        temporaryNodeId = temporaryNodeId,
        parent = parent,
        role = role,
        index = index,
        concept = concept,
        references = references,
        properties = properties,
        children = (children ?: emptyMap()) + (childrenRole to newChildren)
    )

    fun withContainment(newParent: NodeId, newRole: String?, newIndex: Int) = NodeUpdateData(
        nodeId = nodeId,
        temporaryNodeId = temporaryNodeId,
        parent = newParent,
        role = newRole,
        index = newIndex,
        concept = concept,
        references = references,
        properties = properties,
        children = children
    )

    companion object {
        fun newNode(tempId: NodeId, parent: NodeId, role: String?, index: Int, concept: String?) = NodeUpdateData(
            nodeId = null,
            temporaryNodeId = tempId,
            parent = parent,
            role = role,
            index = index,
            concept = concept,
            references = null,
            properties = null,
            children = null
        )

        fun nothing(nodeId: NodeId) = NodeUpdateData(nodeId = nodeId)
    }
}

@Serializable
data class VersionData(
    val repositoryId: String? = null,
    val versionHash: String? = null,
    val rootNodeId: String? = null,
    val nodes: List<NodeData>,
)

@Serializable
data class MessageFromServer(
    val version: VersionData?,
    val replacedIds: Map<String, String>? = null,
) {
    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<MessageFromServer>(json)
    }
}

@Serializable
data class MessageFromClient(
    val changedNodes: List<NodeUpdateData>?,
) {
    fun toJson() = Json.encodeToString(this)
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<MessageFromClient>(json)
    }
}