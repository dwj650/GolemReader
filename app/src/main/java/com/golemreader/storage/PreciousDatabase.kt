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
    entities = [DbMetaEntity::class],
    version = PreciousDatabase.SCHEMA_VERSION,
    exportSchema = true,
)
abstract class PreciousDatabase : RoomDatabase() {
    abstract fun dbMetaDao(): DbMetaDao

    object Migrations {
        val V1_TO_V2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE db_meta ADD COLUMN updated_at_epoch_ms INTEGER NOT NULL DEFAULT 0",
                )
            }
        }
    }

    companion object {
        const val SCHEMA_VERSION = 2
    }
}
