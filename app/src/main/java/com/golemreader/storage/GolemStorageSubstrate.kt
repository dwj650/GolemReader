package com.golemreader.storage

import android.content.Context
import androidx.room.Room
import java.io.Closeable
import java.io.File

data class GolemStorageSubstrate(
    val locations: StorageTierLocations,
    val preciousDatabase: PreciousDatabaseHandle,
    val importedAssets: ImportedAssetStore,
    val rebuildableStore: RebuildableStore,
) {
    companion object {
        fun initialize(
            context: Context,
            databaseName: String = "golem_precious",
            allowMainThreadQueriesForTests: Boolean = false,
        ): GolemStorageSubstrate {
            val importedAssets = File(context.filesDir, "imported-assets").apply { mkdirs() }
            val rebuildableCache = File(context.cacheDir, "rebuildable").apply { mkdirs() }
            val databaseFile = context.getDatabasePath(databaseName).apply {
                parentFile?.mkdirs()
            }

            val locations = StorageTierLocations(
                preciousDatabase = databaseFile,
                importedAssets = importedAssets,
                rebuildableCache = rebuildableCache,
            )

            require(locations.arePreciousAndRebuildableSeparated()) {
                "Precious and rebuildable storage locations must be physically separated."
            }

            val builder = Room.databaseBuilder(
                context.applicationContext,
                PreciousDatabase::class.java,
                databaseName,
            )
                .addMigrations(PreciousDatabase.Migrations.V1_TO_V2)

            if (allowMainThreadQueriesForTests) {
                builder.allowMainThreadQueries()
            }

            val database = builder.build()

            return GolemStorageSubstrate(
                locations = locations,
                preciousDatabase = PreciousDatabaseHandle(database),
                importedAssets = ImportedAssetStore(importedAssets),
                rebuildableStore = RebuildableStore(rebuildableCache),
            )
        }
    }
}

class PreciousDatabaseHandle(private val database: PreciousDatabase) : Closeable {
    fun <T> useDatabase(block: (PreciousDatabase) -> T): T =
        try {
            block(database)
        } finally {
            close()
        }

    override fun close() {
        database.close()
    }
}

class ImportedAssetStore(private val directory: File) {
    init {
        directory.mkdirs()
    }

    fun isEmpty(): Boolean = directory.listFiles().orEmpty().isEmpty()
}
