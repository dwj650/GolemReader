package com.golemreader.storage

import org.junit.Assert.assertEquals
import org.junit.Test

class StoragePlacementTest {
    @Test
    fun placementRuleCoversEveryD15DataType() {
        val expected = mapOf(
            StoredDataType.BookIdentityHash to StorageTier.Precious,
            StoredDataType.ReadingPosition to StorageTier.Precious,
            StoredDataType.AuthoredRule to StorageTier.Precious,
            StoredDataType.Respelling to StorageTier.Precious,
            StoredDataType.RulePackState to StorageTier.Precious,
            StoredDataType.AppSettings to StorageTier.Precious,
            StoredDataType.LibraryCatalog to StorageTier.Precious,
            StoredDataType.SourceAccessRecord to StorageTier.Precious,
            StoredDataType.BookState to StorageTier.Precious,
            StoredDataType.AssetManifestEntry to StorageTier.Precious,
            StoredDataType.StableRuleId to StorageTier.Precious,
            StoredDataType.VoiceModelFile to StorageTier.ImportedAssets,
            StoredDataType.FontFile to StorageTier.ImportedAssets,
            StoredDataType.RulePackFile to StorageTier.ImportedAssets,
            StoredDataType.ThemeFile to StorageTier.ImportedAssets,
            StoredDataType.AudioBufferEntry to StorageTier.Rebuildable,
            StoredDataType.PipelineCacheEntry to StorageTier.Rebuildable,
            StoredDataType.ExtractedImage to StorageTier.Rebuildable,
            StoredDataType.ResolutionRecord to StorageTier.Rebuildable,
        )

        assertEquals(expected.keys, StoredDataType.entries.toSet())
        expected.forEach { (dataType, tier) ->
            assertEquals(dataType.name, tier, StoragePlacementRule.tierFor(dataType))
        }
    }
}
