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

    @Test
    fun migrationFromV2ToV3CreatesBookIdentityAndPreservesDbMeta() {
        helper.createDatabase(TEST_DB_V2_TO_V3, 2).apply {
            execSQL(
                "INSERT INTO db_meta (`key`, value, updated_at_epoch_ms) VALUES ('schema', 'v2', 99)",
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB_V2_TO_V3,
            3,
            true,
            PreciousDatabase.Migrations.V2_TO_V3,
        )

        migrated.query(
            "SELECT value, updated_at_epoch_ms FROM db_meta WHERE `key` = 'schema'",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals("v2", cursor.getString(0))
            assertEquals(99L, cursor.getLong(1))
        }
        migrated.execSQL(
            """
            INSERT INTO book_identity (
                hash,
                algorithm,
                recipe_version,
                created_at_epoch_ms
            ) VALUES (
                'a8e69c8b4b53fb33e34bc2ef16b950f27a1d51a225d2fec4b249f642391565ac',
                'SHA-256',
                1,
                1234
            )
            """.trimIndent(),
        )
        migrated.query("SELECT COUNT(*) FROM book_identity").use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
        }
    }

    @Test
    fun migrationFromV3ToV4CreatesThemeSettingsAndPreservesExistingData() {
        helper.createDatabase(TEST_DB_V3_TO_V4, 3).apply {
            execSQL(
                "INSERT INTO db_meta (`key`, value, updated_at_epoch_ms) VALUES ('schema', 'v3', 99)",
            )
            execSQL(
                """
                INSERT INTO book_identity (
                    hash,
                    algorithm,
                    recipe_version,
                    created_at_epoch_ms
                ) VALUES (
                    'a8e69c8b4b53fb33e34bc2ef16b950f27a1d51a225d2fec4b249f642391565ac',
                    'SHA-256',
                    1,
                    1234
                )
                """.trimIndent(),
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB_V3_TO_V4,
            4,
            true,
            PreciousDatabase.Migrations.V3_TO_V4,
        )

        migrated.query(
            "SELECT value, updated_at_epoch_ms FROM db_meta WHERE `key` = 'schema'",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals("v3", cursor.getString(0))
            assertEquals(99L, cursor.getLong(1))
        }
        migrated.query("SELECT COUNT(*) FROM book_identity").use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
        }
        migrated.query("SELECT choice FROM theme_settings WHERE `key` = 'theme_choice'").use { cursor ->
            cursor.moveToFirst()
            assertEquals("follow_system", cursor.getString(0))
        }
    }

    @Test
    fun destructiveMigrationFromV2ToV3FailsValidation() {
        helper.createDatabase(DESTRUCTIVE_DB_V2_TO_V3, 2).apply {
            execSQL(
                "INSERT INTO db_meta (`key`, value, updated_at_epoch_ms) VALUES ('schema', 'v2', 99)",
            )
            close()
        }

        try {
            helper.runMigrationsAndValidate(
                DESTRUCTIVE_DB_V2_TO_V3,
                3,
                true,
                DESTRUCTIVE_V2_TO_V3,
            )
            fail("Destructive v2 to v3 migration should fail Room schema validation.")
        } catch (_: IOException) {
            // Expected: Room rejects the destructive shape or lost rows.
        } catch (_: IllegalStateException) {
            // Expected: Room rejects the destructive shape or lost rows.
        }
    }

    private companion object {
        const val TEST_DB = "precious-migration-test"
        const val DESTRUCTIVE_DB = "precious-destructive-migration-test"
        const val TEST_DB_V2_TO_V3 = "precious-migration-test-v2-to-v3"
        const val DESTRUCTIVE_DB_V2_TO_V3 = "precious-destructive-migration-test-v2-to-v3"
        const val TEST_DB_V3_TO_V4 = "precious-migration-test-v3-to-v4"

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

        val DESTRUCTIVE_V2_TO_V3 = object : Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE db_meta")
                db.execSQL(
                    """
                    CREATE TABLE book_identity (
                        hash TEXT NOT NULL,
                        algorithm TEXT NOT NULL,
                        recipe_version INTEGER NOT NULL,
                        created_at_epoch_ms INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(hash)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
