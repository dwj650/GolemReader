package com.golemreader.storage

import androidx.room.testing.MigrationTestHelper
import androidx.room.migration.Migration
import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreciousMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        PreciousDatabase::class.java,
    )

    @Test
    fun demonstrationMigrationFromV1ToV2PreservesRows() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO db_meta (`key`, value) VALUES ('schema', 'v1')")
            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            PreciousDatabase.Migrations.V1_TO_V2,
        )

        migrated.query("SELECT value, updated_at_epoch_ms FROM db_meta WHERE `key` = 'schema'").use { cursor ->
            cursor.moveToFirst()
            assertEquals("v1", cursor.getString(0))
            assertEquals(0L, cursor.getLong(1))
        }
    }

    @Test
    fun destructiveMigrationVariantFailsValidation() {
        helper.createDatabase(DESTRUCTIVE_DB, 1).apply {
            execSQL("INSERT INTO db_meta (`key`, value) VALUES ('schema', 'v1')")
            close()
        }

        try {
            helper.runMigrationsAndValidate(
                DESTRUCTIVE_DB,
                2,
                true,
                DESTRUCTIVE_V1_TO_V2,
            )
            fail("Destructive migration should fail Room schema validation.")
        } catch (_: IOException) {
            // Expected: Room rejects the destructive shape or lost rows.
        } catch (_: IllegalStateException) {
            // Expected: Room rejects the destructive shape or lost rows.
        }
    }

    private companion object {
        const val TEST_DB = "precious-migration-test"
        const val DESTRUCTIVE_DB = "precious-destructive-migration-test"

        val DESTRUCTIVE_V1_TO_V2 = object : Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE db_meta")
                db.execSQL(
                    """
                    CREATE TABLE db_meta (
                        `key` TEXT NOT NULL,
                        value TEXT NOT NULL,
                        updated_at_epoch_ms INTEGER NOT NULL,
                        PRIMARY KEY(`key`)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
