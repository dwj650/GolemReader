package com.golemreader.storage

import java.io.File

class RebuildableStore(private val cacheDirectory: File) {
    private val entries = linkedMapOf<String, RebuildableEntry>()

    fun put(
        id: String,
        bytes: ByteArray,
        inUse: Boolean = false,
        nearPlayhead: Boolean = false,
    ) {
        cacheDirectory.mkdirs()
        File(cacheDirectory, id).writeBytes(bytes)
        entries[id] = RebuildableEntry(
            id = id,
            bytes = bytes,
            inUse = inUse,
            nearPlayhead = nearPlayhead,
        )
    }

    fun mark(
        id: String,
        inUse: Boolean? = null,
        nearPlayhead: Boolean? = null,
    ) {
        entries.computeIfPresent(id) { _, entry ->
            entry.copy(
                inUse = inUse ?: entry.inUse,
                nearPlayhead = nearPlayhead ?: entry.nearPlayhead,
            )
        }
    }

    fun contains(id: String): Boolean = entries.containsKey(id)

    fun isEmpty(): Boolean = entries.isEmpty()

    fun clearRam() {
        entries.clear()
    }

    fun evictUnprotected(): EvictionResult {
        val removed = entries.values
            .filterNot { it.inUse || it.nearPlayhead }
            .map { it.id }
            .toSet()

        removed.forEach { id ->
            entries.remove(id)
            File(cacheDirectory, id).delete()
        }

        return EvictionResult(removed)
    }
}

data class EvictionResult(val removedIds: Set<String>)

private data class RebuildableEntry(
    val id: String,
    val bytes: ByteArray,
    val inUse: Boolean,
    val nearPlayhead: Boolean,
)

object CacheClearRoutine {
    fun clearRebuildable(
        tiers: StorageTierLocations,
        rebuildableStore: RebuildableStore,
    ) {
        require(tiers.arePreciousAndRebuildableSeparated()) {
            "Refusing to clear cache because precious and rebuildable locations are not separated."
        }
        tiers.rebuildableCache.deleteChildren()
        tiers.rebuildableCache.mkdirs()
        rebuildableStore.clearRam()
    }
}

private fun File.deleteChildren() {
    if (!exists()) return
    listFiles().orEmpty().forEach { child ->
        if (child.isDirectory) {
            child.deleteRecursively()
        } else {
            child.delete()
        }
    }
}
