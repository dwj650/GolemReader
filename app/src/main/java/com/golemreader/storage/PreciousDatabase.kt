package com.golemreader.storage

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.golemreader.identity.BookIdentityDao
import com.golemreader.identity.BookIdentityEntity
import com.golemreader.theme.ThemeSettingEntity
import com.golemreader.theme.ThemeSettingsDao

@Entity(tableName = "db_meta")
data class DbMetaEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    val value: String,
    @ColumnInfo(name = "updated_at_epoch_ms", defaultValue = "0")
    val updatedAtEpochMs: Long = 0L,
)

@Dao
interface DbMetaDao {
    @Query("SELECT * FROM db_meta ORDER BY `key`")
    fun all(): List<DbMetaEntity>

    @Query("SELECT * FROM db_meta WHERE `key` = :key")
    fun get(key: String): DbMetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: DbMetaEntity)
}

@Database(
    entities = [
        DbMetaEntity::class,
        BookIdentityEntity::class,
        ThemeSettingEntity::class,
    ],
    version = PreciousDatabase.SCHEMA_VERSION,
    exportSchema = true,
)
abstract class PreciousDatabase : RoomDatabase() {
    abstract fun dbMetaDao(): DbMetaDao
    abstract fun bookIdentityDao(): BookIdentityDao
    abstract fun themeSettingsDao(): ThemeSettingsDao

    object Migrations {
        val V1_TO_V2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE db_meta ADD COLUMN updated_at_epoch_ms INTEGER NOT NULL DEFAULT 0",
                )
            }
        }

        val V2_TO_V3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS book_identity (
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

        val V3_TO_V4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS theme_settings (
                        `key` TEXT NOT NULL,
                        choice TEXT NOT NULL,
                        PRIMARY KEY(`key`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO theme_settings (`key`, choice)
                    VALUES ('theme_choice', 'follow_system')
                    """.trimIndent(),
                )
            }
        }

        val ALL = arrayOf(V1_TO_V2, V2_TO_V3, V3_TO_V4)
    }

    companion object {
        const val SCHEMA_VERSION = 4
    }
}
