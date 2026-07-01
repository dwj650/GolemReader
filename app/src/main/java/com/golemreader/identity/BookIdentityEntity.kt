package com.golemreader.identity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "book_identity")
data class BookIdentityEntity(
    @PrimaryKey
    @ColumnInfo(name = "hash")
    val hash: String,
    val algorithm: String,
    @ColumnInfo(name = "recipe_version")
    val recipeVersion: Int,
    @ColumnInfo(name = "created_at_epoch_ms", defaultValue = "0")
    val createdAtEpochMs: Long = 0L,
)

@Dao
interface BookIdentityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfAbsent(entity: BookIdentityEntity): Long

    @Query("SELECT * FROM book_identity WHERE hash = :hash")
    fun get(hash: String): BookIdentityEntity?

    @Query("SELECT COUNT(*) FROM book_identity")
    fun count(): Int
}
