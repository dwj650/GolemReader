package com.golemreader.storage

import java.io.File
import java.nio.file.Path

enum class StorageTier {
    Precious,
    ImportedAssets,
    Rebuildable,
}

enum class StoredDataType {
    BookIdentityHash,
    ReadingPosition,
    AuthoredRule,
    Respelling,
    RulePackState,
    AppSettings,
    LibraryCatalog,
    SourceAccessRecord,
    BookState,
    AssetManifestEntry,
    StableRuleId,
    VoiceModelFile,
    FontFile,
    RulePackFile,
    ThemeFile,
    AudioBufferEntry,
    PipelineCacheEntry,
    ExtractedImage,
    ResolutionRecord,
}

object StoragePlacementRule {
    fun tierFor(dataType: StoredDataType): StorageTier =
        when (dataType) {
            StoredDataType.BookIdentityHash,
            StoredDataType.ReadingPosition,
            StoredDataType.AuthoredRule,
            StoredDataType.Respelling,
            StoredDataType.RulePackState,
            StoredDataType.AppSettings,
            StoredDataType.LibraryCatalog,
            StoredDataType.SourceAccessRecord,
            StoredDataType.BookState,
            StoredDataType.AssetManifestEntry,
            StoredDataType.StableRuleId,
            -> StorageTier.Precious

            StoredDataType.VoiceModelFile,
            StoredDataType.FontFile,
            StoredDataType.RulePackFile,
            StoredDataType.ThemeFile,
            -> StorageTier.ImportedAssets

            StoredDataType.AudioBufferEntry,
            StoredDataType.PipelineCacheEntry,
            StoredDataType.ExtractedImage,
            StoredDataType.ResolutionRecord,
            -> StorageTier.Rebuildable
        }
}

data class StorageTierLocations(
    val preciousDatabase: File,
    val importedAssets: File,
    val rebuildableCache: File,
) {
    fun arePreciousAndRebuildableSeparated(): Boolean =
        !rebuildableCache.canEnumerate(preciousDatabase) &&
            !preciousDatabase.canEnumerate(rebuildableCache) &&
            !preciousDatabase.requireParent().canEnumerate(rebuildableCache)
}

fun File.canEnumerate(other: File): Boolean {
    val selfPath = normalizedPath()
    val otherPath = other.normalizedPath()
    return selfPath == otherPath || otherPath.startsWith(selfPath)
}

private fun File.normalizedPath(): Path = toPath().toAbsolutePath().normalize()

private fun File.requireParent(): File =
    requireNotNull(parentFile) { "Storage path must have a parent: $this" }
